package com.hyf.hotrefresh.remoting;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class MessageFactory {

    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    public static Message createEmptyMessage() {
        Message message = new Message();
        message.setId(idGenerator.incrementAndGet());
        return message;
    }

    public static Message createMessage(RpcMessage rpcMessage) {
        Message message = createEmptyMessage();
        message.setEncoding(RpcMessageConstants.DEFAULT_ENCODING.getCode());
        message.setCodec(RpcMessageConstants.DEFAULT_CODEC.getCode());
        message.setCompress(RpcMessageConstants.DEFAULT_COMPRESSION.getCode());
        message.setMessageType(rpcMessage.getMessageType().getCode());
        message.setBody(rpcMessage);
        return message;
    }
}
