package com.hyf.hotrefresh.core.remoting.payload;

import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcResponse;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class RpcHotRefreshResponse extends RpcResponse {

    public RpcHotRefreshResponse() {
        setStatus(RemotingConstants.RESPONSE_SUCCESS);
    }

    @Override
    public byte getMessageCode() {
        return RpcMessageType.RESPONSE_HOT_REFRESH;
    }
}
