package com.hyf.hotrefresh.remoting;

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
        return null;
    }
}
