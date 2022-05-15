package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.remoting.constants.RpcMessageConstants;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.RpcResponse;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcResponseInst;

import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcResponseHandler extends RpcMessageResponseHandler<RpcResponse> {

    @Override
    public Class<RpcResponse> getRpcMessageClassType() {
        return RpcResponse.class;
    }

    @Override
    public RpcResponse createEmptyRpcMessage() {
        return new RpcResponse();
    }

    @Override
    public void handleResponse(RpcResponse response) throws Exception {

        int status = response.getStatus();
        RpcResponseInst inst = response.getInst();
        byte[] data = response.getData();
        Map<String, Object> extra = response.getExtra();

        if (status == 200 || status == -1) {
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
        String content = new String(response.getData(), RpcMessageConstants.DEFAULT_ENCODING.getCharset());
        Log.warn(content);
    }
}
