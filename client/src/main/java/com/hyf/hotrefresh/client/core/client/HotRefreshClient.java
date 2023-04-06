package com.hyf.hotrefresh.client.core.client;

import com.hyf.hotrefresh.client.api.client.Client;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.Services;
import com.hyf.hotrefresh.common.args.ArgumentHolder;
import com.hyf.hotrefresh.remoting.exception.ClientException;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandler;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandlerFactory;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcBatchRequest;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcHeartbeatRequest;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hyf.hotrefresh.common.Constants.ARG_SERVER_TIMEOUT;
import static com.hyf.hotrefresh.common.Constants.ARG_SERVER_URL;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class HotRefreshClient {

    private static volatile HotRefreshClient INSTANCE;

    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean stopped = new AtomicBoolean(true);

    private final Client         client;
    private final MessageHandler clientMessageHandler;

    private final String serverAddress;
    private final long   timeoutMillis;

    private HotRefreshClient() {

        this.serverAddress = ArgumentHolder.get(ARG_SERVER_URL);
        this.timeoutMillis = ArgumentHolder.get(ARG_SERVER_TIMEOUT);

        List<Client> clients = Services.gets(Client.class);
        if (!clients.isEmpty()) {
            this.client = clients.iterator().next();
        }
        else {
            this.client = new DelegateRoutingClient();
        }

        this.clientMessageHandler = MessageHandlerFactory.getClientMessageHandler();
    }

    public static HotRefreshClient getInstance() {
        if (INSTANCE == null) {
            synchronized (HotRefreshClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HotRefreshClient();
                }
            }
        }
        return INSTANCE;
    }

    public void start() {
        if (!started.compareAndSet(false, true)) {
            return;
        }

        client.start();
        stopped.set(false);
    }

    public void stop() {
        if (!stopped.compareAndSet(false, true)) {
            return;
        }

        try {
            client.stop();
        } catch (Exception e) {
            Log.error("Stop client failed", e);
        }
        started.set(false);
    }

    public void heartbeat() throws ClientException {
        try {
            RpcHeartbeatRequest request = new RpcHeartbeatRequest();
            sendRequest(request);
        } catch (Exception e) {
            throw new ClientException("Failed to connect server: " + serverAddress, e);
        }
    }

    public void sendRequest(RpcMessage request) throws ClientException {
        Message message = MessageFactory.createMessage(request);
        sendAndHandleMessage(message);
    }

    public void sendBatchRequest(List<RpcMessage> rpcRequests) throws ClientException {
        RpcBatchRequest rpcBatchRequest = new RpcBatchRequest();
        rpcBatchRequest.setRpcMessages(rpcRequests);
        Message message = MessageFactory.createMessage(rpcBatchRequest);
        sendAndHandleMessage(message);
    }

    private void sendAndHandleMessage(Message message) throws ClientException {
        Message response = client.sync(serverAddress, message, timeoutMillis);
        try {
            this.clientMessageHandler.handle(response);
        } catch (Exception e) {
            throw new ClientException("Failed to handle response", e);
        }
    }
}
