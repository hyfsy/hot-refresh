package com.hyf.hotrefresh.remoting.rpc.payload;

import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

import java.nio.ByteBuffer;

/**
 * TODO 单例
 *
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcHeartbeatRequest implements RpcMessage {

    @Override
    public ByteBuffer encode(RpcMessageEncoding encoding, RpcMessageCodec codec) {
        return ByteBuffer.allocate(0);
    }

    @Override
    public void decode(ByteBuffer buf, RpcMessageEncoding encoding, RpcMessageCodec codec) {

    }

    @Override
    public byte getMessageCode() {
        return RpcMessageType.REQUEST_HEARTBEAT;
    }
}
