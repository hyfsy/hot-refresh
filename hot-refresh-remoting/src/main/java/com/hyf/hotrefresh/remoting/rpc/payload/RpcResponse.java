package com.hyf.hotrefresh.remoting.rpc.payload;

import com.hyf.hotrefresh.remoting.message.MessageCodec;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class RpcResponse implements RpcMessage {

    // status(4byte)
    // data length(4byte)
    // data
    // extra length(4byte)
    // extra

    public static final int FIXED_LENGTH = 4 + 4 + 4;

    private int                 status;
    private byte[]              data;
    private Map<String, Object> extra;

    @Override
    public ByteBuffer encode(RpcMessageEncoding encoding, RpcMessageCodec codec) {

        int messageLength = FIXED_LENGTH;

        byte[] dataBytes = null;
        if (data != null && data.length != 0) {
            dataBytes = data;
            messageLength += data.length;
        }

        byte[] extraBytes = null;
        if (extra != null && !extra.isEmpty()) {
            extraBytes = MessageCodec.encodeObject(extra, encoding, codec);
            messageLength += extraBytes.length;
        }

        ByteBuffer buf = ByteBuffer.allocate(messageLength);

        buf.putInt(status);

        buf.putInt(dataBytes == null ? 0 : dataBytes.length);
        if (dataBytes != null) {
            buf.put(dataBytes);
        }

        buf.putInt(extraBytes == null ? 0 : extraBytes.length);
        if (extraBytes != null) {
            buf.put(extraBytes);
        }

        return buf;
    }

    @Override
    public void decode(ByteBuffer buf, RpcMessageEncoding encoding, RpcMessageCodec codec) {
        if (!buf.hasRemaining()) {
            return;
        }

        int status = buf.getInt();
        this.setStatus(status);

        int dataLength = buf.getInt();
        if (dataLength != 0) {
            byte[] dataBytes = new byte[dataLength];
            buf.get(dataBytes);
            this.setData(dataBytes);
        }

        int extraLength = buf.getInt();
        if (extraLength != 0) {
            byte[] extraBytes = new byte[extraLength];
            buf.get(extraBytes);
            this.setExtra(MessageCodec.decodeObject(extraBytes, encoding, codec));
        }
    }

    @Override
    public byte getMessageCode() {
        return RpcMessageType.RESPONSE_BASIC;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RpcResponse that = (RpcResponse) o;
        return status == that.status && Objects.equals(extra, that.extra) && Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(extra, status);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
