package com.hyf.hotrefresh.remoting.message.handler;

import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandlerRegistry;
import com.hyf.hotrefresh.remoting.rpc.enums.HandleSide;

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
