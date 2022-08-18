package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandler;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandlerFactory;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcBatchResponse;

import java.util.List;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcBatchResponseHandler extends RpcMessageResponseHandler<RpcBatchResponse> {

    private final MessageHandler clientMessageHandler = MessageHandlerFactory.getClientMessageHandler();

    @Override
    public void handleResponse(RpcBatchResponse response) throws Exception {

        List<RpcMessage> rpcResponses = response.getRpcMessages();
        for (RpcMessage rpcResponse : rpcResponses) {
            Message resp = MessageFactory.createMessage(rpcResponse);
            try {
                clientMessageHandler.handle(resp);
            } catch (Exception e) {
                Log.error("Handle batch response failed", e);
            }
        }
    }
}
