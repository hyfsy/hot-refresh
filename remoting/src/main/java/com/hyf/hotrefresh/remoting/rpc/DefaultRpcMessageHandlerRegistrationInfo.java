package com.hyf.hotrefresh.remoting.rpc;

import com.hyf.hotrefresh.remoting.rpc.enums.HandleSide;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class DefaultRpcMessageHandlerRegistrationInfo implements RpcMessageHandlerRegistrationInfo {

    private byte                                                          handleMessageCode;
    private HandleSide                                                    handleSide;
    private RpcMessageHandler<? extends RpcMessage, ? extends RpcMessage> handler;

    public DefaultRpcMessageHandlerRegistrationInfo(byte handleMessageCode, HandleSide handleSide, RpcMessageHandler<? extends RpcMessage, ? extends RpcMessage> handler) {
        this.handleMessageCode = handleMessageCode;
        this.handleSide = handleSide;
        this.handler = handler;
    }

    @Override
    public byte handleMessageCode() {
        return handleMessageCode;
    }

    @Override
    public HandleSide handleSide() {
        return handleSide;
    }

    @Override
    public RpcMessageHandler<? extends RpcMessage, ? extends RpcMessage> rpcMessageHandler() {
        return handler;
    }
}
