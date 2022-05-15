package com.hyf.hotrefresh.remoting.rpc;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcHeartbeatRequest implements RpcMessage {

    @Override
    public byte[] encode(RpcMessageEncoding encoding, RpcMessageCodec codec) {
        return new byte[0];
    }

    @Override
    public void decode(byte[] bytes, RpcMessageEncoding encoding, RpcMessageCodec codec) {

    }

    @Override
    public RpcMessageType getMessageType() {
        return RpcMessageType.HEARTBEAT_REQUEST;
    }
}
