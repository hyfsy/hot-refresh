package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.remoting.rpc.RpcMessage;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public abstract class RpcMessageResponseHandler<REQ extends RpcMessage> implements RpcMessageHandler<REQ, RpcMessage> {

    @Override
    public RpcMessage handle(REQ response) throws Exception {
        handleResponse(response);
        return null;
    }

    protected abstract void handleResponse(REQ response) throws Exception;
}
