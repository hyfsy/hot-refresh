package com.hyf.hotrefresh.client.core.rpc;

import com.hyf.hotrefresh.client.core.DeferredOpenFileInputStream;
import com.hyf.hotrefresh.client.core.HotRefreshClient;
import com.hyf.hotrefresh.client.watch.Watcher;
import com.hyf.hotrefresh.common.ChangeType;
import com.hyf.hotrefresh.core.remoting.payload.RpcHotRefreshRequest;
import com.hyf.hotrefresh.core.remoting.payload.RpcRequestInst;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class RpcPushWatcher extends Thread implements Watcher {

    private final BlockingQueue<RpcMessage> rpcMessageQueue = new LinkedBlockingQueue<>();

    private final HotRefreshClient client = HotRefreshClient.getInstance();

    private volatile boolean closed = false;

    public RpcPushWatcher() {
        start();
    }

    @Override
    public boolean interest(Object context) {
        if (!(context instanceof Path)) {
            return false;
        }

        Path p = (Path) context;
        return p.toString().endsWith(".java");
    }

    @Override
    public void onChange(File file, ChangeType type) {
        addFileChangeRequest(file, type);
    }

    @Override
    public void stopWatch() {
        closed = true;
    }

    @Override
    public void run() {
        while (!closed) {
            handleFileChangeRequest();

            // 避免文件修改触发的抖动
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void addFileChangeRequest(File file, ChangeType type) {
        try {
            RpcHotRefreshRequest request = new RpcHotRefreshRequest();
            request.setFileName(file.getName());
            request.setFileLocation(file.getAbsolutePath());
            request.setInst(RpcRequestInst.valueOf(type.name()));
            // 延迟打开的输入流，防止文件被长时间占用
            request.setBody(new DeferredOpenFileInputStream(file));
            rpcMessageQueue.put(request);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void handleFileChangeRequest() {
        List<RpcMessage> messages = new ArrayList<>();
        int i = rpcMessageQueue.drainTo(messages);
        if (i == 0) {
            return;
        }

        messages = new ArrayList<>(new HashSet<>(messages));

        if (messages.size() == 1) {
            client.sendRequest(messages.get(0));
        }
        else {
            client.sendBatchRequest(messages);
        }
    }
}
