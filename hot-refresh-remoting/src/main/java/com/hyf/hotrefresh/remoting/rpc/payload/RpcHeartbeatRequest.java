package com.hyf.hotrefresh.remoting.rpc.payload;

import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

/**
 * TODO 单例
 *
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
    public byte getMessageCode() {
        return RpcMessageType.REQUEST_HEARTBEAT;
    }
}
