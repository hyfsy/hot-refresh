package com.hyf.hotrefresh.remoting.message.handler;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.rpc.RpcErrorResponse;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.handler.RpcMessageHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public abstract class AbstractMessageHandler implements MessageHandler {

    /** message type -> handler */
    private final Map<Byte, RpcMessageHandler<? extends RpcMessage, ? extends RpcMessage>> handlers = new HashMap<>();

    public void registerMessageHandler(RpcMessageType messageType) {
        handlers.put(messageType.getCode(), messageType.getRpcMessageHandler());
    }

    @Override
    public Message handle(Message request) throws Exception {
        try {
            RpcMessage rpcMessage = (RpcMessage) request.getBody();
            RpcMessageHandler<RpcMessage, RpcMessage> rpcMessageHandler = (RpcMessageHandler<RpcMessage, RpcMessage>) handlers.get(request.getMessageType());
            if (!rpcMessageHandler.getRpcMessageClassType().isInstance(rpcMessage)) {
                throw new IllegalStateException("Rpc message type related handler not exist: " + rpcMessage.getClass());
            }
            RpcMessage response = rpcMessageHandler.handle(rpcMessage);
            return MessageFactory.createMessage(response);
        } catch (Throwable t) {
            if (Log.isDebugMode()) {
                Log.error("Handle message failed", t);
            }
            RpcErrorResponse rpcErrorResponse = new RpcErrorResponse();
            rpcErrorResponse.setThrowable(t);
            return MessageFactory.createMessage(rpcErrorResponse);
        }
    }
}
