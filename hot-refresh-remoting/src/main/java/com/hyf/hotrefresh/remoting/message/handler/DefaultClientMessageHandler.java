package com.hyf.hotrefresh.remoting.message.handler;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class DefaultClientMessageHandler extends AbstractMessageHandler {

    @Override
    public void init() {
        registerMessageHandler(RpcMessageType.RESPONSE);
        registerMessageHandler(RpcMessageType.BATCH_RESPONSE);
        registerMessageHandler(RpcMessageType.HEARTBEAT_RESPONSE);
        registerMessageHandler(RpcMessageType.SUCCESS_RESPONSE);
        registerMessageHandler(RpcMessageType.ERROR_RESPONSE);
    }
}
