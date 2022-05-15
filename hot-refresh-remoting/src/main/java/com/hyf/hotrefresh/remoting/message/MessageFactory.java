package com.hyf.hotrefresh.remoting.message;

import com.hyf.hotrefresh.remoting.constants.RpcMessageConstants;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class MessageFactory {

    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    public static Message createEmptyMessage() {
        return createMessage(null);
    }

    public static Message createMessage(RpcMessage rpcMessage) {

        Message message = new Message();
        message.setId(idGenerator.incrementAndGet());
        message.setEncoding(RpcMessageConstants.DEFAULT_ENCODING.getCode());
        message.setCodec(RpcMessageConstants.DEFAULT_CODEC.getCode());
        message.setCompress(RpcMessageConstants.DEFAULT_COMPRESSION.getCode());

        if (rpcMessage == null) {
            return message;
        }

        message.setMessageType(rpcMessage.getMessageType().getCode());
        message.setBody(rpcMessage);
        return message;
    }
}
