package com.hyf.hotrefresh.remoting;

import java.util.HashMap;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class DefaultMessageHandler implements MessageHandler {

    private final Map<Byte, RpcMessageHandler> handlers = new HashMap<>();

    public DefaultMessageHandler() {
        initHandler();
    }

    private void initHandler() {
        handlers.put(RpcMessageType.REQUEST.getCode(), new RpcRequestHandler());
        handlers.put(RpcMessageType.RESPONSE.getCode(), new RpcResponseHandler());
        handlers.put(RpcMessageType.BATCH_REQUEST.getCode(), new RpcBatchRequestHandler());
    }

    @Override
    public void handle(Message message) throws Exception {
        RpcMessage rpcMessage = RpcMessageType.getMessageType(message.getMessageType()).createMessage();
        RpcMessageHandler rpcMessageHandler = handlers.get(message.getMessageType());
        if (!rpcMessageHandler.getRpcMessageClassType().isInstance(rpcMessage)) {
            throw new IllegalStateException("rpc message type related handler not exist: " + rpcMessage.getClass());
        }
        rpcMessageHandler.handle(rpcMessage);
    }
}
