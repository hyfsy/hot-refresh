package com.hyf.hotrefresh.remoting.rpc;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcResponse;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcSuccessResponse extends RpcResponse {

    @Override
    public byte getMessageCode() {
        return RpcMessageType.RESPONSE_SUCCESS;
    }
}
