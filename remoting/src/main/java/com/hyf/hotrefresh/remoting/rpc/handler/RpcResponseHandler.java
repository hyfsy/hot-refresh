package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcResponse;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcResponseHandler extends RpcMessageResponseHandler<RpcResponse> {

    @Override
    public void handleResponse(RpcResponse response) throws Exception {

        int status = response.getStatus();

        if (status == RpcResponse.SUCCESS || status == RpcResponse.UNKNOWN) {
            handleSuccessResponse(response);
        }
        else {
            handleErrorResponse(response);
        }
    }

    protected void handleSuccessResponse(RpcResponse response) throws Exception {
        Log.info("success");
    }

    protected void handleErrorResponse(RpcResponse response) throws Exception {
        String content = new String(response.getData(), RemotingConstants.DEFAULT_ENCODING.getCharset());
        Log.warn(content);
    }
}
