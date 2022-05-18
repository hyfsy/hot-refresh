package com.hyf.hotrefresh.client.core;

import com.hyf.hotrefresh.client.core.rpc.RpcClient;
import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.util.ExceptionUtils;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcBatchRequest;

import java.util.List;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class HotRefreshClient {

    public static final String SERVER_ADDRESS = Constants.PUSH_SERVER_URL;

    private static final HotRefreshClient INSTANCE = new HotRefreshClient();

    private final RpcClient client;

    private HotRefreshClient() {
        client = RpcClient.getInstance();
    }

    public static HotRefreshClient getInstance() {
        return INSTANCE;
    }

    public void sendRequest(RpcMessage request) {
        try {
            Message message = MessageFactory.createMessage(request);
            client.sync(SERVER_ADDRESS, message);
        } catch (Exception e) {
            Log.warn("Request to " + SERVER_ADDRESS + " failed: " + ExceptionUtils.getNestedMessage(e));
            Log.debug(ExceptionUtils.getStackMessage(e));
        }
    }

    public void sendBatchRequest(List<RpcMessage> rpcRequests) {
        try {
            RpcBatchRequest rpcBatchRequest = new RpcBatchRequest();
            rpcBatchRequest.setRpcMessages(rpcRequests);
            Message message = MessageFactory.createMessage(rpcBatchRequest);
            client.sync(SERVER_ADDRESS, message);
        } catch (Exception e) {
            Log.warn("Request to " + SERVER_ADDRESS + " failed: " + ExceptionUtils.getNestedMessage(e));
            Log.debug(ExceptionUtils.getStackMessage(e));
        }
    }
}
