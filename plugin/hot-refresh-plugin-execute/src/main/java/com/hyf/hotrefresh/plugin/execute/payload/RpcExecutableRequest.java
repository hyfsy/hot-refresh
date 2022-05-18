package com.hyf.hotrefresh.plugin.execute.payload;

import com.hyf.hotrefresh.remoting.rpc.payload.RpcRequest;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

/**
 * @author baB_hyf
 * @date 2022/05/17
 */
public class RpcExecutableRequest extends RpcRequest {

    @Override
    public byte getMessageCode() {
        return RpcMessageType.REQUEST_EXECUTABLE;
    }
}
