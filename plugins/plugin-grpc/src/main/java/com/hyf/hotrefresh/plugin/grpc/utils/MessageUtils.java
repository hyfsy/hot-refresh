package com.hyf.hotrefresh.plugin.grpc.utils;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageFactory;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCompression;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;

import java.nio.ByteBuffer;

/**
 * @author baB_hyf
 * @date 2022/10/01
 */
public abstract class MessageUtils {

    public static Message convert(com.hyf.hotrefresh.plugin.grpc.generate.Message message) {
        if (message == null) {
            return null;
        }

        Message msg = new Message();
        msg.setId(message.getId());
        msg.setEncoding(message.getEncoding().byteAt(0));
        msg.setCodec(message.getCodec().byteAt(0));
        msg.setCompress(message.getCompress().byteAt(0));
        msg.setMessageType(message.getMessageType().byteAt(0));
        msg.setMetadata(message.getMetadataMap());

        RpcMessage rpcMessage = RpcMessageFactory.createRpcMessage(msg.getMessageType());
        byte[] data = message.getBody().getValue().toByteArray();
        data = RpcMessageCompression.getCompression(msg.getCompress()).decompress(data);
        rpcMessage.decode(ByteBuffer.wrap(data), RpcMessageEncoding.getEncoding(msg.getEncoding()), RpcMessageCodec.getCodec(msg.getCodec()));
        msg.setBody(rpcMessage);
        return msg;
    }

    public static com.hyf.hotrefresh.plugin.grpc.generate.Message convert(Message message) {
        if (message == null) {
            return null;
        }

        RpcMessage rpcMessage = (RpcMessage) message.getBody();
        byte[] data = new byte[0];
        if (rpcMessage != null) {
            ByteBuffer buf = rpcMessage.encode(RpcMessageEncoding.getEncoding(message.getEncoding()), RpcMessageCodec.getCodec(message.getCodec()));
            data = buf.array();
        }
        data = RpcMessageCompression.getCompression(message.getCompress()).compress(data);

        return com.hyf.hotrefresh.plugin.grpc.generate.Message.newBuilder()
                .setId(message.getId())
                .setEncoding(ByteString.copyFrom(new byte[]{message.getEncoding()}))
                .setCodec(ByteString.copyFrom(new byte[]{message.getCodec()}))
                .setCompress(ByteString.copyFrom(new byte[]{message.getCompress()}))
                .setMessageType(ByteString.copyFrom(new byte[]{message.getMessageType()}))
                .putAllMetadata(message.getMetadata())
                .setBody(Any.newBuilder().setValue(ByteString.copyFrom(data)))
                .build();
    }
}
