package com.hyf.hotrefresh.core.remoting.payload;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcRequest;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class RpcHotRefreshBatchRequest extends RpcRequest {

    // super content
    // file number(4byte)
    // RpcHotRefreshRequest content

    public static final int FIXED_LENGTH = 4;

    private final List<RpcHotRefreshRequest> requests = new ArrayList<>();

    public void addRequest(RpcHotRefreshRequest request) {
        requests.add(request);
    }

    public List<RpcHotRefreshRequest> getRequests() {
        return requests;
    }

    @Override
    public ByteBuffer encode(RpcMessageEncoding encoding, RpcMessageCodec codec) {

        ByteBuffer superBuf = super.encode(encoding, codec);

        int messageLength = superBuf.limit() + FIXED_LENGTH;

        List<ByteBuffer> requestBuffers = new ArrayList<>();
        for (RpcHotRefreshRequest request : requests) {
            ByteBuffer encoded = request.encode(encoding, codec);
            requestBuffers.add(encoded);
            messageLength += encoded.limit();
        }

        ByteBuffer buf = ByteBuffer.allocate(messageLength);

        superBuf.flip(); // 可写
        buf.put(superBuf);

        buf.putInt(requestBuffers.size());
        for (ByteBuffer requestBuffer : requestBuffers) {
            requestBuffer.flip();
            buf.put(requestBuffer);
        }

        return buf;
    }

    @Override
    public void decode(ByteBuffer buf, RpcMessageEncoding encoding, RpcMessageCodec codec) {

        super.decode(buf, encoding, codec);

        int requestNumber = buf.getInt();
        while (requestNumber-- > 0) {
            RpcHotRefreshRequest request = new RpcHotRefreshRequest();
            request.decode(buf, encoding, codec);
            this.addRequest(request);
        }
    }

    @Override
    public byte getMessageCode() {
        return RpcMessageType.REQUEST_BATCH_HOT_REFRESH;
    }

}
