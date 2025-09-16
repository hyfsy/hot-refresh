package com.hyf.hotrefresh.client.core.rpc;

import com.hyf.hotrefresh.client.api.watch.Watcher;
import com.hyf.hotrefresh.client.core.DeferredOpenFileInputStream;
import com.hyf.hotrefresh.client.core.client.HotRefreshClient;
import com.hyf.hotrefresh.common.ChangeType;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.core.remoting.payload.RpcHotRefreshBatchRequest;
import com.hyf.hotrefresh.core.remoting.payload.RpcHotRefreshRequest;
import com.hyf.hotrefresh.core.remoting.payload.RpcHotRefreshRequestInst;
import com.hyf.hotrefresh.remoting.exception.ClientException;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class RpcPushWatcher extends Thread implements Watcher {

    private final BlockingQueue<RpcHotRefreshRequest> rpcMessageQueue = new ArrayBlockingQueue<>(1000);

    private final HotRefreshClient client = HotRefreshClient.getInstance();

    private volatile boolean closed = false;

    private volatile boolean enableBatch = false;

    public void setEnableBatch(boolean enableBatch) {
        this.enableBatch = enableBatch;
    }

    public boolean isEnableBatch() {
        return enableBatch;
    }

    @Override
    public boolean interest(Object context) {
        if (!(context instanceof Path)) {
            return false;
        }

        Path p = (Path) context;
        return p.toString().endsWith(".java") || p.toString().endsWith(".class");
    }

    @Override
    public void onChange(File file, ChangeType type) {
        addFileChangeRequest(file, type);
    }

    @Override
    public void startWatch() {
        start();
    }

    @Override
    public void stopWatch() {
        closed = true;
    }

    @Override
    public void run() {
        while (!closed) {
            try {
                handleFileChangeRequest();
            } catch (Throwable t) {
                Log.error("Hotrefresh request handle failed", t);
            }
        }
    }

    private void addFileChangeRequest(File file, ChangeType type) {
        try {
            RpcHotRefreshRequest request = new RpcHotRefreshRequest();
            request.setFileName(file.getName());
            request.setFileLocation(file.getAbsolutePath());
            request.setInst(RpcHotRefreshRequestInst.valueOf(type.name()));
            // 延迟打开的输入流，防止文件被长时间占用
            request.setBody(new DeferredOpenFileInputStream(file));
            rpcMessageQueue.put(request);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void handleFileChangeRequest() throws ClientException {
        try {
            List<RpcHotRefreshRequest> messages = new ArrayList<>();
            messages.add(rpcMessageQueue.take());

            // 特殊处理：编译的时候内部类先编译完成，然后主类过一段时间才编译完成的情况，主子类都要收集
            while (true) {
                int snapshotSize = messages.size();
                preventFluctuation();
                rpcMessageQueue.drainTo(messages);
                if (snapshotSize == messages.size()) {
                    break;
                }
            }

            messages = mergeRequest(messages);

            if (Log.isDebugMode()) {
                Log.debug("Changed file: \n" + messages.stream().map(RpcHotRefreshRequest::toString).collect(Collectors.joining("\n")));
            }

            if (messages.size() == 1) {
                client.sendRequest(messages.get(0));
            }
            else {
                if (enableBatch) {
                    client.sendRequest(toBatch(messages));
                }
                else {
                    client.sendBatchRequest((List) messages);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void preventFluctuation() {
        // 避免文件修改触发的抖动
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private List<RpcHotRefreshRequest> mergeRequest(List<RpcHotRefreshRequest> messages) {

        // 去重
        messages = new ArrayList<>(new HashSet<>(messages));

        Map<String, Map<RpcHotRefreshRequestInst, RpcHotRefreshRequest>> temp = new LinkedHashMap<>();
        for (RpcHotRefreshRequest message : messages) {
            temp.putIfAbsent(message.getFileLocation(), new HashMap<>());
            temp.get(message.getFileLocation()).put(message.getInst(), message);
        }

        List<RpcHotRefreshRequest> result = new ArrayList<>();
        for (Map.Entry<String, Map<RpcHotRefreshRequestInst, RpcHotRefreshRequest>> entry : temp.entrySet()) {
            int size = entry.getValue().size();
            if (size == 1) {
                result.addAll(entry.getValue().values());
            }
            // 新增、修改、删除同时存在的情况
            else {
                result.add(Optional.ofNullable(entry.getValue().get(RpcHotRefreshRequestInst.DELETE)) // 优先删除
                .orElseGet(() -> Optional.ofNullable(entry.getValue().get(RpcHotRefreshRequestInst.MODIFY)) // 其次修改
                .orElseGet(() -> entry.getValue().get(RpcHotRefreshRequestInst.CREATE)))); // 最后创建
            }
        }

        // 文件从大到小排序，主要因为内部类的情况，让主类先编译好
        result.sort(Comparator.comparing(r -> r.getFileLocation().substring(0, r.getFileLocation().lastIndexOf("."))));

        return result;
    }

    private RpcHotRefreshBatchRequest toBatch(List<RpcHotRefreshRequest> messages) {
        RpcHotRefreshBatchRequest request = new RpcHotRefreshBatchRequest();
        for (RpcHotRefreshRequest message : messages) {
            request.addRequest(message);
        }
        return request;
    }
}
