package com.hyf.hotrefresh.core.remoting.payload;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcRequest;

/**
 * @author baB_hyf
 * @date 2022/06/25
 */
public class RpcHotRefreshCommandRequest extends RpcRequest {

    @Override
    public byte getMessageCode() {
        return RpcMessageType.REQUEST_COMMAND_HOT_REFRESH;
    }
}
