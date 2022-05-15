package com.hyf.hotrefresh.server.http;

import com.hyf.hotrefresh.common.ChangeType;
import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.util.ExceptionUtils;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.remoting.Message;
import com.hyf.hotrefresh.remoting.MessageFactory;
import com.hyf.hotrefresh.remoting.RpcRequest;
import com.hyf.hotrefresh.remoting.RpcRequestInst;
import com.hyf.hotrefresh.server.watch.Watcher;

import java.io.*;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class HttpPushWatcher extends Thread implements Watcher {

    private final Map<String, File> changedFileMap = new ConcurrentHashMap<>();

    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();

    private final HttpClient client = HttpClient.getInstance();

    private volatile boolean closed = false;

    public HttpPushWatcher() {
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
        addRequest(file, type);
    }

    @Override
    public void stopWatch() {
        closed = true;
    }

    @Override
    public void run() {
        while (!closed) {
            sendRequest();

            // push(purge());
            // try {
            //     TimeUnit.SECONDS.sleep(1);
            // } catch (InterruptedException ignored) {
            // }
        }
    }

    // TODO name
    private void addRequest(File file, ChangeType type) {
        try {
            RpcRequest request = new RpcRequest();
            request.setFileName(file.getName());
            request.setFileLocation(file.getAbsolutePath());
            request.setInst(RpcRequestInst.valueOf(type.name()));
            request.setContent(new FileInputStream(file));

            Message message = MessageFactory.createMessage(request);
            messageQueue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("add request failed", e);
        }

        // changedFileMap.put(file.getName() + Constants.FILE_NAME_SEPARATOR + type, file);
    }

    private void sendRequest() {
        try {
            Message message = messageQueue.take();
            client.sync(Constants.PUSH_SERVER_URL, message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            Log.warn("Request to " + Constants.PUSH_SERVER_URL + " failed: " + ExceptionUtils.getNestedMessage(e));
            Log.debug(ExceptionUtils.getStackMessage(e));
        }
    }

    private Map<String, File> purge() {
        Map<String, File> fileMap = new LinkedHashMap<>(changedFileMap);
        changedFileMap.clear();
        return fileMap;
    }

    private void push(Map<String, File> fileMap) {
        if (fileMap.isEmpty()) {
            return;
        }

        InputStream is;
        try {
            is = client.upload(Constants.PUSH_SERVER_URL, fileMap);
        } catch (IOException e) {
            Log.warn("Upload to " + Constants.PUSH_SERVER_URL + " failed: " + ExceptionUtils.getNestedMessage(e));
            Log.debug(ExceptionUtils.getStackMessage(e));
            return;
        }

        if (is == null) {
            return; // no content
        }

        try {
            String content = IOUtils.readAsString(is);
            handleResponseContent(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleResponseContent(String content) {
        int idx = content.indexOf(Constants.MESSAGE_SEPARATOR);
        if (idx == -1) {
            Log.info("success");
            return;
        }

        if (Log.isDebugMode()) {
            String debugMessage = content.substring(idx + Constants.MESSAGE_SEPARATOR.length());
            Log.debug(debugMessage);
        }
        else {
            String warnMessage = content.substring(0, idx);
            Log.warn(warnMessage);
            String debugMessage = content.substring(idx + Constants.MESSAGE_SEPARATOR.length());
            Log.debug(debugMessage);
        }
    }
}
