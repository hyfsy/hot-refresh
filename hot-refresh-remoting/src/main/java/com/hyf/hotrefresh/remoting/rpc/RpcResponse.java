package com.hyf.hotrefresh.remoting.rpc;

import com.hyf.hotrefresh.remoting.message.MessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcResponseInst;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
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
    // inst(1byte)
    // extra length(4byte)
    // extra

    public static final int FIXED_LENGTH = 4 + 4 + 1 + 4;

    private Map<String, Object> extra  = new HashMap<>();
    private int                 status = -1;
    private byte[]              data   = new byte[0];
    private RpcResponseInst     inst   = RpcResponseInst.NONE;

    @Override
    public byte[] encode(RpcMessageEncoding encoding, RpcMessageCodec codec) {

        byte[] extraBytes = MessageCodec.encodeObject(extra, encoding, codec);

        int messageLength = FIXED_LENGTH + data.length + extraBytes.length;

        ByteBuffer buf = ByteBuffer.allocate(messageLength);
        buf.putInt(status);
        buf.putInt(data.length);
        buf.put(data);
        buf.put(inst.getCode());
        buf.putInt(extraBytes.length);
        buf.put(extraBytes);
        return buf.array();
    }

    @Override
    public void decode(byte[] bytes, RpcMessageEncoding encoding, RpcMessageCodec codec) {
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        if (!buf.hasRemaining()) {
            return;
        }

        int status = buf.getInt();
        int dataLength = buf.getInt();
        byte[] dataBytes = new byte[dataLength];
        buf.get(dataBytes);
        byte instCode = buf.get();
        int extraLength = buf.getInt();
        byte[] extraBytes = new byte[extraLength];
        buf.get(extraBytes);

        this.setStatus(status);
        this.setData(dataBytes);
        this.setInst(RpcResponseInst.getInst(instCode));
        this.setExtra(MessageCodec.decodeObject(extraBytes, encoding, codec));
    }

    @Override
    public RpcMessageType getMessageType() {
        return RpcMessageType.RESPONSE;
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

    public RpcResponseInst getInst() {
        return inst;
    }

    public void setInst(RpcResponseInst inst) {
        this.inst = inst;
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
        return status == that.status && Objects.equals(extra, that.extra) && Arrays.equals(data, that.data) && inst == that.inst;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(extra, status, inst);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
