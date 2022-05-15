package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.remoting.rpc.RpcErrorResponse;

import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcErrorResponseHandler extends RpcMessageResponseHandler<RpcErrorResponse> {

    @Override
    public Class<RpcErrorResponse> getRpcMessageClassType() {
        return RpcErrorResponse.class;
    }

    @Override
    public RpcErrorResponse createEmptyRpcMessage() {
        return new RpcErrorResponse();
    }

    @Override
    protected void handleResponse(RpcErrorResponse response) throws Exception {
        Map<String, Object> extra = response.getExtra();
        if (Log.isDebugMode()) {
            String stackMessage = (String) extra.get(RpcErrorResponse.STACK_MESSAGE);
            Log.debug(stackMessage);
        }
        else {
            String nestedMessage = (String) extra.get(RpcErrorResponse.NESTED_MESSAGE);
            Log.warn(nestedMessage);
            String stackMessage = (String) extra.get(RpcErrorResponse.STACK_MESSAGE);
            Log.debug(stackMessage);
        }
    }
}
