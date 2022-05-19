package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandler;
import com.hyf.hotrefresh.remoting.rpc.RpcSuccessResponse;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcRequest;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcResponse;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcRequestHandler implements RpcMessageHandler<RpcRequest, RpcResponse> {

    @Override
    public RpcResponse handle(RpcRequest request) throws Exception {
        return new RpcSuccessResponse();
    }
}
