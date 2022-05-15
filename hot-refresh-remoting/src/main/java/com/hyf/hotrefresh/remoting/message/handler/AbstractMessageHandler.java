package com.hyf.hotrefresh.remoting.message.handler;

import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
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
    private final Map<Byte, RpcMessageHandler> handlers = new HashMap<>();

    public AbstractMessageHandler() {
        initHandler();
    }

    protected abstract void initHandler();

    public void addHandler(Byte messageType, RpcMessageHandler handler) {
        handlers.put(messageType, handler);
    }

    @Override
    public Message handle(Message request) throws Exception {
        RpcMessage rpcMessage = RpcMessageType.getMessageType(request.getMessageType()).createMessage();
        RpcMessageHandler rpcMessageHandler = handlers.get(request.getMessageType());
        if (!rpcMessageHandler.getRpcMessageClassType().isInstance(rpcMessage)) {
            throw new IllegalStateException("Rpc message type related handler not exist: " + rpcMessage.getClass());
        }
        RpcMessage response = rpcMessageHandler.handle(rpcMessage);
        return MessageFactory.createMessage(response);
    }
}
