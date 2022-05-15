package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.remoting.rpc.RpcHeartbeatRequest;
import com.hyf.hotrefresh.remoting.rpc.RpcHeartbeatResponse;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcHeartbeatRequestHandler implements RpcMessageHandler<RpcHeartbeatRequest, RpcHeartbeatResponse> {

    @Override
    public Class<RpcHeartbeatRequest> getRpcMessageClassType() {
        return RpcHeartbeatRequest.class;
    }

    @Override
    public RpcHeartbeatRequest createEmptyRpcMessage() {
        return new RpcHeartbeatRequest();
    }

    @Override
    public RpcHeartbeatResponse handle(RpcHeartbeatRequest request) throws Exception {
        return new RpcHeartbeatResponse();
    }
}
