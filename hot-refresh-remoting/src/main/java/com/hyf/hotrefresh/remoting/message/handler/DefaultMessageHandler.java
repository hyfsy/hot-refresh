package com.hyf.hotrefresh.remoting.message.handler;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

import java.util.List;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class DefaultMessageHandler extends AbstractMessageHandler {

    private final List<RpcMessageType> rpcMessageTypes;

    public DefaultMessageHandler(List<RpcMessageType> rpcMessageTypes) {
        this.rpcMessageTypes = rpcMessageTypes;
    }

    @Override
    public void init() {
        rpcMessageTypes.forEach(this::registerMessageHandler);
    }
}
