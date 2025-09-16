package com.hyf.hotrefresh.core.remoting.payload;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcResponse;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class RpcHotRefreshBatchResponse extends RpcResponse {

    @Override
    public byte getMessageCode() {
        return RpcMessageType.RESPONSE_BATCH_HOT_REFRESH;
    }
}
