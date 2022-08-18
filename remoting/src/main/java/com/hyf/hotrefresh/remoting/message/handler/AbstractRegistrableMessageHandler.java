package com.hyf.hotrefresh.remoting.message.handler;

import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandler;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandlerRegistry;

import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/17
 */
public abstract class AbstractRegistrableMessageHandler extends AbstractMessageHandler {

    protected RpcMessageHandlerRegistry registry;

    public AbstractRegistrableMessageHandler(RpcMessageHandlerRegistry registry) {
        this.registry = registry;
    }

    protected void registerAll(Map<Byte, RpcMessageHandler<?, ?>> handlers) {
        handlers.forEach((code, handler) -> getHandlers().put(code, handler));
    }
}
