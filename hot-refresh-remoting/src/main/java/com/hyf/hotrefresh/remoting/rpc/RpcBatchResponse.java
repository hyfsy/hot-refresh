package com.hyf.hotrefresh.remoting.rpc;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcBatchResponse extends RpcBatchMessage {

    @Override
    public RpcMessageType getMessageType() {
        return RpcMessageType.BATCH_RESPONSE;
    }
}
