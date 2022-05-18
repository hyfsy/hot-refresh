package com.hyf.hotrefresh.remoting.message.handler;

import com.hyf.hotrefresh.remoting.rpc.enums.HandleSide;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandlerRegistry;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class DefaultClientMessageHandler extends AbstractRegistrableMessageHandler {

    public DefaultClientMessageHandler(RpcMessageHandlerRegistry registry) {
        super(registry);
        registerAll(registry.getHandlersByHandleSide(HandleSide.CLIENT));
    }
}
