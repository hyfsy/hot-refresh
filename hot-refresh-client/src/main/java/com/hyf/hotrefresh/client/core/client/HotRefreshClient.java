package com.hyf.hotrefresh.client.core.client;

import com.hyf.hotrefresh.common.args.ArgumentHolder;
import com.hyf.hotrefresh.common.hook.Disposable;
import com.hyf.hotrefresh.common.hook.ShutdownHook;
import com.hyf.hotrefresh.common.util.UrlUtils;
import com.hyf.hotrefresh.remoting.exception.ClientException;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandler;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandlerFactory;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcBatchRequest;

import java.util.List;

import static com.hyf.hotrefresh.common.Constants.*;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class HotRefreshClient implements Disposable {

    private static volatile HotRefreshClient INSTANCE;

    private final Client         client;
    private final MessageHandler clientMessageHandler;

    private final long   timeoutMillis;
    private       String serverAddress;

    private HotRefreshClient() {

        // rpc -> ip:port
        this.serverAddress = ArgumentHolder.get(ARG_SERVER_URL);

        // http -> http://xxx
        if (isHttp(serverAddress)) {
            serverAddress = UrlUtils.concat(serverAddress, REFRESH_API);
        }

        this.timeoutMillis = ArgumentHolder.get(ARG_SERVER_TIMEOUT);

        this.client = new DelegateRoutingClient(serverAddress);

        this.clientMessageHandler = MessageHandlerFactory.getClientMessageHandler();
    }

    public static HotRefreshClient getInstance() {
        if (INSTANCE == null) {
            synchronized (HotRefreshClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = createClient();
                }
            }
        }
        return INSTANCE;
    }

    private static HotRefreshClient createClient() {
        HotRefreshClient client = new HotRefreshClient();
        client.start();
        return client;
    }

    private void start() {
        client.start();
        ShutdownHook.getInstance().addDisposable(this);
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

    private boolean isHttp(String address) {
        return address.startsWith("http");
    }

    @Override
    public void destroy() throws Exception {
        client.stop();
    }
}
