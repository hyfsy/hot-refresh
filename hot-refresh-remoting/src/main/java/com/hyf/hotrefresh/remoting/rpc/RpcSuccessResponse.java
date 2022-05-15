package com.hyf.hotrefresh.remoting.rpc;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcResponseInst;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcSuccessResponse extends RpcResponse {

    public RpcSuccessResponse() {
        setStatus(200);
        setInst(RpcResponseInst.LOG);
    }

    @Override
    public RpcMessageType getMessageType() {
        return RpcMessageType.SUCCESS_RESPONSE;
    }

}
