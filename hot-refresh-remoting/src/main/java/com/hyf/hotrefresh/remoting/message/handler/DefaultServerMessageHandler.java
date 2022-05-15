package com.hyf.hotrefresh.remoting.message.handler;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class DefaultServerMessageHandler extends AbstractMessageHandler {

    @Override
    public void init() {
        registerMessageHandler(RpcMessageType.REQUEST);
        registerMessageHandler(RpcMessageType.BATCH_REQUEST);
        registerMessageHandler(RpcMessageType.HEARTBEAT_REQUEST);
    }
}
