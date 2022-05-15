package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandler;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandlerFactory;
import com.hyf.hotrefresh.remoting.rpc.RpcBatchRequest;
import com.hyf.hotrefresh.remoting.rpc.RpcBatchResponse;
import com.hyf.hotrefresh.remoting.rpc.RpcErrorResponse;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcBatchRequestHandler implements RpcMessageHandler<RpcBatchRequest, RpcBatchResponse> {

    private final MessageHandler serverMessageHandler = MessageHandlerFactory.getServerMessageHandler();

    @Override
    public Class<RpcBatchRequest> getRpcMessageClassType() {
        return RpcBatchRequest.class;
    }

    @Override
    public RpcBatchRequest createEmptyRpcMessage() {
        return new RpcBatchRequest();
    }

    @Override
    public RpcBatchResponse handle(RpcBatchRequest request) throws Exception {

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
