package com.hyf.hotrefresh.plugin.execute.payload;

import com.hyf.hotrefresh.core.remoting.payload.RpcHotRefreshRequest;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

/**
 * @author baB_hyf
 * @date 2022/05/17
 */
public class RpcExecutableRequest extends RpcHotRefreshRequest {

    @Override
    public byte getMessageCode() {
        return RpcMessageType.REQUEST_EXECUTABLE;
    }
}
