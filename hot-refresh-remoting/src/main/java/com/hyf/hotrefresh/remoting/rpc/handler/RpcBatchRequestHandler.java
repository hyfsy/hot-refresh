package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.remoting.rpc.RpcBatchRequest;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.RpcResponse;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcBatchRequestHandler implements RpcMessageHandler {

    @Override
    public Class<?> getRpcMessageClassType() {
        return RpcBatchRequest.class;
    }

    @Override
    public RpcMessage handle(RpcMessage rpcMessage) throws Exception {
        return new RpcResponse();
    }
}
