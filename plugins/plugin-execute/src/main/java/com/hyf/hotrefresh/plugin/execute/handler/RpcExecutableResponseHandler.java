package com.hyf.hotrefresh.plugin.execute.handler;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import com.hyf.hotrefresh.remoting.rpc.handler.RpcResponseHandler;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcResponse;

import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/17
 */
public class RpcExecutableResponseHandler extends RpcResponseHandler {

    @Override
    protected void handleSuccessResponse(RpcResponse response) throws Exception {
        String message = new String(response.getData(), RemotingConstants.DEFAULT_ENCODING.getCharset());
        Log.info("execute result: \r\n" + message);
    }

    @Override
    protected void handleErrorResponse(RpcResponse response) throws Exception {
        Map<String, Object> extra = response.getExtra();
        String message = (String) extra.get(RemotingConstants.EXTRA_EXCEPTION_STACK);
        Log.warn(message);
    }
}
