package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.remoting.constants.RpcMessageConstants;
import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.RpcResponse;

import java.nio.charset.Charset;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcResponseHandler extends RpcMessageResponseHandler {

    @Override
    public Class<?> getRpcMessageClassType() {
        return RpcResponse.class;
    }

    @Override
    public void handleResponse(RpcMessage rpcMessage) throws Exception {
        RpcResponse response = (RpcResponse) rpcMessage;
        byte[] data = response.getData();
        Charset charset = RpcMessageConstants.DEFAULT_ENCODING.getCharset();
        String content = new String(data, charset);

        int idx = content.indexOf(Constants.MESSAGE_SEPARATOR);
        if (idx == -1) {
            Log.info("success");
            return;
        }

        if (Log.isDebugMode()) {
            String debugMessage = content.substring(idx + Constants.MESSAGE_SEPARATOR.length());
            Log.debug(debugMessage);
        }
        else {
            String warnMessage = content.substring(0, idx);
            Log.warn(warnMessage);
            String debugMessage = content.substring(idx + Constants.MESSAGE_SEPARATOR.length());
            Log.debug(debugMessage);
        }
    }
}
