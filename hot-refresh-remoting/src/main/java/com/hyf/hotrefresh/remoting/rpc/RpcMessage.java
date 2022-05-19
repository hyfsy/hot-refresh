package com.hyf.hotrefresh.remoting.rpc;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;

import java.nio.ByteBuffer;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public interface RpcMessage {

    ByteBuffer encode(RpcMessageEncoding encoding, RpcMessageCodec codec);

    void decode(ByteBuffer buf, RpcMessageEncoding encoding, RpcMessageCodec codec);

    byte getMessageCode();
}
