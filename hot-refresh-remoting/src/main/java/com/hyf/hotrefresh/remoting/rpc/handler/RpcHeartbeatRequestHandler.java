package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandler;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcHeartbeatRequest;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcHeartbeatResponse;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcHeartbeatRequestHandler implements RpcMessageHandler<RpcHeartbeatRequest, RpcHeartbeatResponse> {

    @Override
    public RpcHeartbeatResponse handle(RpcHeartbeatRequest request) throws Exception {
        return new RpcHeartbeatResponse();
    }
}
