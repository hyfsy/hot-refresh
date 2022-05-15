package com.hyf.hotrefresh.remoting;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public interface RpcMessage {

    byte[] encode(RpcMessageEncoding encoding, RpcMessageCodec codec);

    void decode(byte[] bytes, RpcMessageEncoding encoding, RpcMessageCodec codec);

    RpcMessageType getMessageType();
}
