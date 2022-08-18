package com.hyf.hotrefresh.plugin.execute.payload;

import com.hyf.hotrefresh.remoting.rpc.payload.RpcResponse;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

/**
 * @author baB_hyf
 * @date 2022/05/17
 */
public class RpcExecutableResponse extends RpcResponse {

    @Override
    public byte getMessageCode() {
        return RpcMessageType.RESPONSE_EXECUTABLE;
    }
}
