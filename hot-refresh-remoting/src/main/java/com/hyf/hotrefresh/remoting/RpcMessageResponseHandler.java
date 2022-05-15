package com.hyf.hotrefresh.remoting;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public abstract class RpcMessageResponseHandler implements RpcMessageHandler {

    @Override
    public RpcMessage handle(RpcMessage rpcMessage) throws Exception {
        handleResponse(rpcMessage);
        return null;
    }

    protected abstract void handleResponse(RpcMessage rpcMessage) throws Exception;
}
