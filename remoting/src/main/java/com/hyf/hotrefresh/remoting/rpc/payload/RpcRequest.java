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
import java.util.Map;
import java.util.Objects;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public abstract class RpcRequest implements RpcMessage {

    // header length(4byte)
    // header
    // body length(4byte)
    // body

    public static final int FIXED_LENGTH = 4 + 4;

    private Map<String, Object> headers;
    private InputStream         body;

    @Override
    public ByteBuffer encode(RpcMessageEncoding encoding, RpcMessageCodec codec) {

        int messageLength = FIXED_LENGTH;

        byte[] headerBytes = null;
        if (headers != null && !headers.isEmpty()) {
            headerBytes = MessageCodec.encodeObject(headers, encoding, codec);
            messageLength += headerBytes.length;
        }

        byte[] contentBytes = null;
        if (body != null) {
            try {
                contentBytes = IOUtils.readAsByteArray(body, true);
                messageLength += contentBytes.length;
            } catch (IOException e) {
                throw new RuntimeException("Read input stream failed", e);
            }
        }

        ByteBuffer buf = ByteBuffer.allocate(messageLength);

        buf.putInt(headerBytes == null ? 0 : headerBytes.length);
        if (headerBytes != null) {
            buf.put(headerBytes);
        }

        buf.putInt(contentBytes == null ? 0 : contentBytes.length);
        if (contentBytes != null) {
            buf.put(contentBytes);
        }

        return buf;
    }

    @Override
    public void decode(ByteBuffer buf, RpcMessageEncoding encoding, RpcMessageCodec codec) {

        int headerLength = buf.getInt();
        if (headerLength != 0) {
            byte[] headerBytes = new byte[headerLength];
            buf.get(headerBytes);
            this.setHeaders(MessageCodec.decodeObject(headerBytes, encoding, codec));
        }

        int bodyLength = buf.getInt();
        if (bodyLength != 0) {
            byte[] bodyBytes = new byte[bodyLength];
            buf.get(bodyBytes);
            this.setBody(new ByteArrayInputStream(bodyBytes));
        }
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
