package com.hyf.hotrefresh.remoting.message.handler;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.remoting.exception.RpcException;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageFactory;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandler;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcErrorResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public abstract class AbstractMessageHandler implements MessageHandler {

    /** message type -> handler */
    private Map<Byte, RpcMessageHandler<? extends RpcMessage, ? extends RpcMessage>> handlers = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public Message handle(Message request) throws Exception {
        try {
            RpcMessage rpcMessage = (RpcMessage) request.getBody();
            RpcMessageHandler<RpcMessage, RpcMessage> rpcMessageHandler = (RpcMessageHandler<RpcMessage, RpcMessage>) getHandlers().get(request.getMessageType());
            if (rpcMessageHandler == null) {
                throw new RpcException("Unknown message type code: " + request.getMessageType());
            }
            Class<RpcMessage> rpcMessageClassType = RpcMessageFactory.getRpcMessageClassType((Class<? extends RpcMessageHandler<?, ?>>) rpcMessageHandler.getClass());
            if (!(rpcMessageClassType == rpcMessage.getClass())) {
                throw new IllegalStateException("Rpc message type related handler not exist: " + rpcMessage.getClass());
            }
            RpcMessage response = rpcMessageHandler.handle(rpcMessage);
            return MessageFactory.createResponseMessage(request, response);
        } catch (Throwable t) {
            if (Log.isDebugMode()) {
                Log.error("Handle message failed", t);
            }
            RpcErrorResponse rpcErrorResponse = new RpcErrorResponse();
            rpcErrorResponse.setThrowable(t);
            return MessageFactory.createResponseMessage(request, rpcErrorResponse);
        }
    }

    protected Map<Byte, RpcMessageHandler<? extends RpcMessage, ? extends RpcMessage>> getHandlers() {
        return handlers;
    }
}
