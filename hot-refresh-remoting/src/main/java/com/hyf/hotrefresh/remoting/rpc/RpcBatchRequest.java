package com.hyf.hotrefresh.remoting.rpc;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageCodec;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class RpcBatchRequest implements RpcMessage {

    private final List<RpcRequest> rpcRequests = new ArrayList<>();

    @Override
    public byte[] encode(RpcMessageEncoding encoding, RpcMessageCodec codec) {
        return new byte[0];
    }

    @Override
    public void decode(byte[] bytes, RpcMessageEncoding encoding, RpcMessageCodec codec) {
    }

    @Override
    public RpcMessageType getMessageType() {
        return RpcMessageType.BATCH_REQUEST;
    }

}
