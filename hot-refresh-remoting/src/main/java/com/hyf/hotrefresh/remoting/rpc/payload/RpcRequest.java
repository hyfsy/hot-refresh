package com.hyf.hotrefresh.remoting.rpc.payload;

import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class RpcRequest implements RpcMessage {

    // header length(4byte)
    // header
    // body length(4byte)
    // body

    public static final int FIXED_LENGTH = 4 + 4;

    private Map<String, Object> headers = new HashMap<>();
    private InputStream         body;

    @Override
    public ByteBuffer encode(RpcMessageEncoding encoding, RpcMessageCodec codec) {

        byte[] headerBytes = MessageCodec.encodeObject(headers, encoding, codec);

        byte[] contentBytes;
        try {
            contentBytes = IOUtils.readAsByteArray(body);
        } catch (IOException e) {
            throw new RuntimeException("Read input stream failed", e);
        } finally {
            IOUtils.close(body);
        }

        int messageLength = FIXED_LENGTH + headerBytes.length + contentBytes.length;

        ByteBuffer buf = ByteBuffer.allocate(messageLength);

        buf.putInt(headerBytes.length);
        buf.put(headerBytes);
        buf.putInt(contentBytes.length);
        buf.put(contentBytes);

        return buf;
    }

    @Override
    public void decode(ByteBuffer buf, RpcMessageEncoding encoding, RpcMessageCodec codec) {

        int headerLength = buf.getInt();
        byte[] headerBytes = new byte[headerLength];
        buf.get(headerBytes);

        int bodyLength = buf.getInt();
        byte[] bodyBytes = new byte[bodyLength];
        buf.get(bodyBytes);

        this.setHeaders(MessageCodec.decodeObject(headerBytes, encoding, codec));
        this.setBody(new ByteArrayInputStream(bodyBytes));
    }

    @Override
    public byte getMessageCode() {
        return RpcMessageType.REQUEST_BASIC;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public InputStream getBody() {
        return body;
    }

    public void setBody(InputStream body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RpcRequest that = (RpcRequest) o;
        return Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers);
    }
}
