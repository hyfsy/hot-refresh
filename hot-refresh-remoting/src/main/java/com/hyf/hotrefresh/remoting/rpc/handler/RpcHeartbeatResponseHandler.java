package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.remoting.rpc.RpcHeartbeatResponse;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcHeartbeatResponseHandler extends RpcMessageResponseHandler<RpcHeartbeatResponse> {

    @Override
    public Class<RpcHeartbeatResponse> getRpcMessageClassType() {
        return RpcHeartbeatResponse.class;
    }

    @Override
    public RpcHeartbeatResponse createEmptyRpcMessage() {
        return new RpcHeartbeatResponse();
    }

    @Override
    protected void handleResponse(RpcHeartbeatResponse response) throws Exception {

    }
}
