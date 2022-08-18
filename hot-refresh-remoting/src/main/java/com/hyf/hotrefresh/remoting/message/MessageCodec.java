package com.hyf.hotrefresh.remoting.message;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import com.hyf.hotrefresh.remoting.exception.CodecException;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageFactory;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCompression;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class MessageCodec {

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

    // HYF
    public static final byte[] MAGIC = {(byte) 0x48, (byte) 0x59, (byte) 0x46};

    public static final int FIXED_LENGTH = MAGIC.length + 1 + 4 + 4 + 1 + 1 + 1 + 1 + 4 + 4;

    public static byte[] encode(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("message is null");
        }

        try {
            RpcMessage rpcMessage = (RpcMessage) message.getBody();

            RpcMessageCodec codec = RpcMessageCodec.getCodec(message.getCodec());
            RpcMessageEncoding encoding = RpcMessageEncoding.getEncoding(message.getEncoding());
            RpcMessageCompression compression = RpcMessageCompression.getCompression(message.getCompress());

            // header
            Map<String, Object> headMap = message.getHeaderMap();
            byte[] headerData = encodeObject(headMap, encoding, codec);
            headerData = compression.compress(headerData);

            // body
            byte[] data = new byte[0];
            if (rpcMessage != null) {
                ByteBuffer buf = rpcMessage.encode(encoding, codec);
                data = buf.array();
            }
            data = compression.compress(data);

            if (FIXED_LENGTH + headerData.length + data.length > Integer.MAX_VALUE) {
                throw new CodecException("Message size reaches limit");
            }

            int messageLength = FIXED_LENGTH + headerData.length + data.length;
            ByteBuffer buf = ByteBuffer.allocate(messageLength);
            buf.put(MAGIC);
            buf.put(RemotingConstants.MESSAGE_VERSION);
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
        } catch (Exception e) {
            Log.error("Failed to encode message: " + message.toString(), e);
            throw e;
        }
    }

    public static Message decode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("bytes is null");
        }

        ByteBuffer buf = ByteBuffer.wrap(bytes);
        return decode(buf);
    }

    public static Message decode(ByteBuffer buf) {
        if (buf == null) {
            throw new IllegalArgumentException("buf is null");
        }

        try {
            byte[] magic = new byte[MAGIC.length];
            buf.get(magic);
            if (!Arrays.equals(magic, MAGIC)) {
                throw new CodecException("Unknown message");
            }

            byte version = buf.get();
            if (version != RemotingConstants.MESSAGE_VERSION) {
                throw new CodecException("Unknown message version: " + version);
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

            // header
            int headerLength = buf.getInt();
            byte[] headerData = new byte[headerLength];
            buf.get(headerData);
            headerData = compression.decompress(headerData);
            Map<String, Object> headerMap = decodeObject(headerData, encoding, codec);

            // body
            int bodyLength = buf.getInt();
            byte[] data = new byte[bodyLength];
            buf.get(data);
            data = compression.decompress(data);

            RpcMessage rpcMessage = RpcMessageFactory.createRpcMessage(messageTypeCode);
            ByteBuffer dataBuf = ByteBuffer.wrap(data);
            rpcMessage.decode(dataBuf, encoding, codec);

            Message message = MessageFactory.createEmptyMessage();
            message.setId(id);
            message.setEncoding(encodingCode);
            message.setCodec(codecCode);
            message.setCompress(compressionCode);
            message.setMessageType(messageTypeCode);
            message.setHeaderMap(headerMap);
            message.setBody(rpcMessage);
            return message;
        } catch (Exception e) {
            buf.flip();
            byte[] bytes = new byte[buf.limit()];
            buf.get(bytes);
            Log.error("Failed to decode message: " + Arrays.toString(bytes), e);
            throw e;
        }
    }

    public static byte[] encodeObject(Object obj, RpcMessageEncoding encoding, RpcMessageCodec codec) {
        if (obj == null) {
            return new byte[0];
        }

        RpcMessageCodec.JdkCodec jdkCodec = new RpcMessageCodec.JdkCodec();
        return jdkCodec.encode(obj);
    }

    public static <T> T decodeObject(byte[] data, RpcMessageEncoding encoding, RpcMessageCodec codec) {
        if (data == null || data.length == 0) {
            return null;
        }

        RpcMessageCodec.JdkCodec jdkCodec = new RpcMessageCodec.JdkCodec();
        return jdkCodec.decode(data);
    }
}
