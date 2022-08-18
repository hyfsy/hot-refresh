package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcErrorResponse;

import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcErrorResponseHandler extends RpcMessageResponseHandler<RpcErrorResponse> {

    @Override
    protected void handleResponse(RpcErrorResponse response) throws Exception {
        Map<String, Object> extra = response.getExtra();
        if (Log.isDebugMode()) {
            String stackMessage = (String) extra.get(RemotingConstants.EXTRA_EXCEPTION_STACK);
            Log.debug(stackMessage);
        }
        else {
            String nestedMessage = (String) extra.get(RemotingConstants.EXTRA_EXCEPTION_NESTED);
            Log.warn(nestedMessage);
            String stackMessage = (String) extra.get(RemotingConstants.EXTRA_EXCEPTION_STACK);
            Log.debug(stackMessage);
        }
    }
}
