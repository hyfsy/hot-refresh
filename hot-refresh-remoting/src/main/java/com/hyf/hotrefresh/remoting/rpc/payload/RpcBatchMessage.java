package com.hyf.hotrefresh.remoting.rpc.payload;

import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageFactory;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public abstract class RpcBatchMessage implements RpcMessage {

    // segment size(4byte)
    // segment message type(1byte)
    // segment length(4byte)
    // segment
    // segment2 message type(1byte)
    // segment2 length(4byte)
    // segment2
    // ...

    private List<RpcMessage> rpcMessages = new ArrayList<>();

    @Override
    public ByteBuffer encode(RpcMessageEncoding encoding, RpcMessageCodec codec) {

        int len = 0;

        List<MessageSegment> segments = new ArrayList<>();

        for (RpcMessage rpcMessage : rpcMessages) {
            ByteBuffer buf = rpcMessage.encode(encoding, codec);
            len += buf.limit();
            buf.flip(); // 可写

            MessageSegment segment = new MessageSegment();
            segment.code = rpcMessage.getMessageCode();
            segment.length = buf.limit();
            segment.buf = buf;
            segments.add(segment);
        }

        ByteBuffer buf = ByteBuffer.allocate(4 + 4 * rpcMessages.size() + rpcMessages.size() + len);
        buf.putInt(rpcMessages.size());

        for (MessageSegment segment : segments) {
            buf.put(segment.code);
            buf.putInt(segment.length);
            if (segment.length != 0) {
                buf.put(segment.buf);
            }
        }

        return buf;
    }

    @Override
    public void decode(ByteBuffer buf, RpcMessageEncoding encoding, RpcMessageCodec codec) {

        int segmentSize = buf.getInt();
        List<RpcMessage> segmentList = new ArrayList<>(segmentSize);

        while (--segmentSize >= 0) {
            byte messageTypeCode = buf.get();

            int segmentLength = buf.getInt();
            if (segmentLength != 0) {
                byte[] segmentBytes = new byte[segmentLength];
                buf.get(segmentBytes);
                RpcMessage rpcMessage = RpcMessageFactory.createRpcMessage(messageTypeCode);
                ByteBuffer rpcMessageBuf = ByteBuffer.wrap(segmentBytes);
                rpcMessage.decode(rpcMessageBuf, encoding, codec);
                segmentList.add(rpcMessage);
            }
        }

        this.setRpcMessages(segmentList);
    }

    public List<RpcMessage> getRpcMessages() {
        return rpcMessages;
    }

    public void setRpcMessages(List<RpcMessage> rpcMessages) {
        this.rpcMessages = new ArrayList<>(new HashSet<>(rpcMessages));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RpcBatchMessage that = (RpcBatchMessage) o;
        return Objects.equals(rpcMessages, that.rpcMessages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rpcMessages);
    }

    private static class MessageSegment {
        private int        length;
        private ByteBuffer buf;
        private byte       code;
    }
}
