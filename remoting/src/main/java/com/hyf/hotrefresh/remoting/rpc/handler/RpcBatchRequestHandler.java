package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandler;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandlerFactory;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandler;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcBatchRequest;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcBatchResponse;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcErrorResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcBatchRequestHandler implements RpcMessageHandler<RpcBatchRequest, RpcBatchResponse> {

    @Override
    public RpcBatchResponse handle(RpcBatchRequest request) throws Exception {

        MessageHandler serverMessageHandler = MessageHandlerFactory.getServerMessageHandler();

        List<RpcMessage> rpcResponses = new ArrayList<>();

        List<RpcMessage> rpcRequests = request.getRpcMessages();
        for (RpcMessage rpcRequest : rpcRequests) {
            Message req = MessageFactory.createMessage(rpcRequest);
            try {
                Message rtn = serverMessageHandler.handle(req);
                Object body = rtn.getBody();
                if (body instanceof RpcMessage) {
                    rpcResponses.add((RpcMessage) body);
                }
                else {
                    Log.warn("Current message not RpcMessage instance, so it's cannot use RpcBatchRequest to send request: " + body.toString());
                    RpcErrorResponse rpcErrorResponse = new RpcErrorResponse();
                    rpcErrorResponse.setThrowable(new RuntimeException("Return message not RpcMessage instance"));
                    rpcResponses.add(rpcErrorResponse);
                }
            } catch (Exception e) {
                RpcErrorResponse rpcErrorResponse = new RpcErrorResponse();
                rpcErrorResponse.setThrowable(e);
                rpcResponses.add(rpcErrorResponse);
            }
        }

        RpcBatchResponse response = new RpcBatchResponse();
        response.setRpcMessages(rpcResponses);
        return response;
    }
}
