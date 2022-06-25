package com.hyf.hotrefresh.core.remoting.payload;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcResponse;

/**
 * @author baB_hyf
 * @date 2022/06/25
 */
public class RpcHotRefreshCommandResponse extends RpcResponse {

    @Override
    public byte getMessageCode() {
        return RpcMessageType.RESPONSE_COMMAND_HOT_REFRESH;
    }
}
