package com.hyf.hotrefresh.client.core;

import com.hyf.hotrefresh.client.core.rpc.RpcClient;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.args.ArgumentHolder;
import com.hyf.hotrefresh.common.util.ExceptionUtils;
import com.hyf.hotrefresh.common.util.UrlUtils;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcBatchRequest;

import java.util.List;

import static com.hyf.hotrefresh.common.Constants.ARG_SERVER_URL;
import static com.hyf.hotrefresh.common.Constants.REFRESH_API;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class HotRefreshClient {

    private static final HotRefreshClient INSTANCE = new HotRefreshClient();

    private final RpcClient client;

    private String serverAddress;

    private HotRefreshClient() {
        client = RpcClient.getInstance();
        serverAddress = UrlUtils.concat(ArgumentHolder.get(ARG_SERVER_URL), REFRESH_API);
    }

    public static HotRefreshClient getInstance() {
        return INSTANCE;
    }

    public void sendRequest(RpcMessage request) {
        Message message = MessageFactory.createMessage(request);
        sendMessage(message);
    }

    public void sendBatchRequest(List<RpcMessage> rpcRequests) {
        RpcBatchRequest rpcBatchRequest = new RpcBatchRequest();
        rpcBatchRequest.setRpcMessages(rpcRequests);
        Message message = MessageFactory.createMessage(rpcBatchRequest);
        sendMessage(message);
    }

    private void sendMessage(Message message) {
        try {
            client.sync(serverAddress, message);
        } catch (Exception e) {
            if (Log.isDebugMode()) {
                Log.debug(ExceptionUtils.getStackMessage(e));
            }
            else {
                Log.warn("Request to " + serverAddress + " failed: " + ExceptionUtils.getNestedMessage(e));
            }
        }
    }
}
