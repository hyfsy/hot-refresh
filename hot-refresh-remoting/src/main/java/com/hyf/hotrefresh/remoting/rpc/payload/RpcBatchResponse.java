package com.hyf.hotrefresh.remoting.rpc.payload;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class RpcBatchResponse extends RpcBatchMessage {

    @Override
    public byte getMessageCode() {
        return RpcMessageType.RESPONSE_BATCH;
    }
}
