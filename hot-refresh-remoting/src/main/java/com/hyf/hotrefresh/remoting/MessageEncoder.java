package com.hyf.hotrefresh.remoting;

import com.hyf.hotrefresh.common.Constants;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class MessageEncoder {

    public static final byte[] MAGIC = "HYF".getBytes(Constants.MESSAGE_ENCODING);

    public static final byte VERSION = 1;

    // magic(x byte)
    // version(1byte)
    // message length(4byte)
    // id(4byte)
    // encoding(1byte)
    // codec(1byte)
    // compress(1byte)
    // message type(1byte)
    // header length(4byte)
    // header data
    // data body length(4byte)
    // data body

    public static final int FIXED_LENGTH = MAGIC.length + 1 + 4 + 4 + 1 + 1 + 1 + 1 + 4;

    public static byte[] encode(Message message) {

        Object body = message.getBody();
        if (body == null) {
            throw new IllegalArgumentException("body is null");
        }
        if (!(body instanceof RpcMessage)) {
            throw new IllegalArgumentException("Body not support: " + body.getClass());
        }

        RpcMessage rpcMessage = (RpcMessage) body;

        RpcMessageCodec codec = RpcMessageCodec.getCodec(message.getCodec());
        RpcMessageEncoding encoding = RpcMessageEncoding.getEncoding(message.getEncoding());
        RpcMessageCompression compression = RpcMessageCompression.getCompression(message.getCompress());

        // header
        Map<String, Object> headMap = message.getHeaderMap();
        byte[] headerData = encodeMap(headMap, encoding, codec);
        headerData = compression.compress(headerData);

        // body
        byte[] data = rpcMessage.encode(encoding, codec);
        data = compression.compress(data);

        if (FIXED_LENGTH + headerData.length + data.length > Integer.MAX_VALUE) {
            throw new IllegalStateException("Message size reaches limit");
        }

        int messageLength = FIXED_LENGTH + headerData.length + data.length;
        ByteBuffer buf = ByteBuffer.allocate(messageLength);
        buf.put(MAGIC);
        buf.put(VERSION);
        buf.putInt(messageLength);
        buf.putInt(message.getId());
        buf.put(message.getEncoding());
        buf.put(message.getCodec());
        buf.put(message.getCompress());
        buf.put(message.getMessageType());
        buf.putInt(headerData.length);
        buf.put(headerData);
        buf.putInt(data.length);
        buf.put(data);
        return buf.array();
    }

    public static Message decode(byte[] bytes) {
        ByteBuffer buf = ByteBuffer.wrap(bytes);

        byte[] magic = new byte[MAGIC.length];
        buf.get(magic);
        if (magic != MAGIC) {
            throw new IllegalArgumentException("Unknown message: " + Arrays.toString(bytes));
        }

        byte version = buf.get();
        if (version != VERSION) {
            throw new IllegalArgumentException("Unknown message version: " + version);
        }

        int messageLength = buf.getInt();

        int id = buf.getInt();

        byte encodingCode = buf.get();
        RpcMessageEncoding encoding = RpcMessageEncoding.getEncoding(encodingCode);
        byte codecCode = buf.get();
        RpcMessageCodec codec = RpcMessageCodec.getCodec(codecCode);
        byte compressionCode = buf.get();
        RpcMessageCompression compression = RpcMessageCompression.getCompression(compressionCode);
        byte messageTypeCode = buf.get();
        RpcMessageType messageType = RpcMessageType.getMessageType(messageTypeCode);

        // header
        int headerLength = buf.getInt();
        byte[] headerData = new byte[headerLength];
        buf.get(headerData);
        headerData = compression.decompress(headerData);
        Map<String, Object> headerMap = decodeMap(headerData, encoding, codec);

        // body
        int bodyLength = buf.getInt();
        byte[] data = new byte[bodyLength];
        buf.get(data);
        data = compression.decompress(data);

        RpcMessage rpcMessage = messageType.createMessage();
        rpcMessage.decode(data, encoding, codec);

        Message message = MessageFactory.createEmptyMessage();
        message.setEncoding(encodingCode);
        message.setCodec(codecCode);
        message.setCompress(compressionCode);
        message.setMessageType(messageTypeCode);
        message.setHeaderMap(headerMap);
        message.setBody(rpcMessage);
        return message;
    }

    public static byte[] encodeMap(Map<String, Object> map, RpcMessageEncoding encoding, RpcMessageCodec codec) {
        RpcMessageCodec.JdkCodec jdkCodec = new RpcMessageCodec.JdkCodec();
        return jdkCodec.encode(map);
    }

    public static Map<String, Object> decodeMap(byte[] data, RpcMessageEncoding encoding, RpcMessageCodec codec) {
        RpcMessageCodec.JdkCodec jdkCodec = new RpcMessageCodec.JdkCodec();
        return jdkCodec.decode(data);
    }
}
