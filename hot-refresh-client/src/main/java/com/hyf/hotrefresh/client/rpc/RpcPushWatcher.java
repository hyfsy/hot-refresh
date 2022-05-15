package com.hyf.hotrefresh.client.rpc;

import com.hyf.hotrefresh.client.watch.Watcher;
import com.hyf.hotrefresh.common.ChangeType;
import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.util.ExceptionUtils;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.rpc.RpcBatchRequest;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.RpcRequest;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcRequestInst;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();

    private final RpcClient client = RpcClient.getInstance();

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
            throw new RuntimeException("Add request failed", e);
        }
    }

    private void handleFileChangeRequest() {
        List<Message> messages = new ArrayList<>();
        int i = messageQueue.drainTo(messages);
        if (i == 0) {
            return;
        }

        messages = new ArrayList<>(new HashSet<>(messages));

        if (i == 1) {
            sendRequest(messages.get(0));
        }
        else {
            sendBatchRequest(messages);
        }
    }

    private void sendRequest(Message message) {
        try {
            client.sync(Constants.PUSH_SERVER_URL, message);
        } catch (Exception e) {
            Log.warn("Request to " + Constants.PUSH_SERVER_URL + " failed: " + ExceptionUtils.getNestedMessage(e));
            Log.debug(ExceptionUtils.getStackMessage(e));
        }
    }

    private void sendBatchRequest(List<Message> messages) {
        List<RpcMessage> rpcRequests = new ArrayList<>();
        for (Message message : messages) {
            Object body = message.getBody();
            if (body instanceof RpcMessage) {
                rpcRequests.add((RpcMessage) body);
            }
            else {
                Log.warn("Current message not RpcMessage instance, so it's cannot use RpcBatchRequest to send request: " + body.toString());
            }
        }

        try {
            RpcBatchRequest rpcBatchRequest = new RpcBatchRequest();
            rpcBatchRequest.setRpcMessages(rpcRequests);
            Message message = MessageFactory.createMessage(rpcBatchRequest);
            client.sync(Constants.PUSH_SERVER_URL, message);
        } catch (Exception e) {
            Log.warn("Request to " + Constants.PUSH_SERVER_URL + " failed: " + ExceptionUtils.getNestedMessage(e));
            Log.debug(ExceptionUtils.getStackMessage(e));
        }
    }
}
