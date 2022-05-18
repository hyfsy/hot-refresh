package com.hyf.hotrefresh.remoting.rpc;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public interface RpcMessage {

    byte[] encode(RpcMessageEncoding encoding, RpcMessageCodec codec);

    void decode(byte[] bytes, RpcMessageEncoding encoding, RpcMessageCodec codec);

    default RpcMessageType getMessageType() {return null;}

    byte getMessageCode();
}
