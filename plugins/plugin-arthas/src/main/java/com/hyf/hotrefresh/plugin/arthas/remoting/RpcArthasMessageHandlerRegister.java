package com.hyf.hotrefresh.plugin.arthas.remoting;

import com.hyf.hotrefresh.remoting.rpc.DefaultRpcMessageHandlerRegistrationInfo;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandlerRegister;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandlerRegistry;
import com.hyf.hotrefresh.remoting.rpc.enums.HandleSide;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

public class RpcArthasMessageHandlerRegister implements RpcMessageHandlerRegister {
    @Override
    public void register(RpcMessageHandlerRegistry registry) {
        registry.register(new DefaultRpcMessageHandlerRegistrationInfo(RpcMessageType.REQUEST_COMMAND_ARTHAS, HandleSide.SERVER, new RpcArthasCommandRequestHandler()));
        registry.register(new DefaultRpcMessageHandlerRegistrationInfo(RpcMessageType.RESPONSE_COMMAND_ARTHAS, HandleSide.CLIENT, new RpcArthasCommandResponseHandler()));
    }
}
