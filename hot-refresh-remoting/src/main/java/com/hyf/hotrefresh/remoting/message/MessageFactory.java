package com.hyf.hotrefresh.remoting.message;

import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
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
        message.setEncoding(RemotingConstants.DEFAULT_ENCODING.getCode());
        message.setCodec(RemotingConstants.DEFAULT_CODEC.getCode());
        message.setCompress(RemotingConstants.DEFAULT_COMPRESSION.getCode());

        if (rpcMessage == null) {
            return message;
        }

        message.setMessageType(rpcMessage.getMessageCode());
        message.setBody(rpcMessage);
        return message;
    }

    public static Message createResponseMessage(Message request, RpcMessage rpcMessage) {
        Message message = new Message();
        message.setId(request.getId());
        message.setEncoding(request.getEncoding());
        message.setCodec(request.getCodec());
        message.setCompress(request.getCompress());

        if (rpcMessage == null) {
            return message;
        }

        message.setMessageType(rpcMessage.getMessageCode());
        message.setBody(rpcMessage);
        return message;
    }
}
