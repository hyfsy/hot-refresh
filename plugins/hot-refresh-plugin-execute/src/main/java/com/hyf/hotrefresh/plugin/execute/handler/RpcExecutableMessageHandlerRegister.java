package com.hyf.hotrefresh.plugin.execute.handler;

import com.hyf.hotrefresh.remoting.rpc.DefaultRpcMessageHandlerRegistrationInfo;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandlerRegister;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandlerRegistry;
import com.hyf.hotrefresh.remoting.rpc.enums.HandleSide;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

/**
 * @author baB_hyf
 * @date 2022/05/17
 */
public class RpcExecutableMessageHandlerRegister implements RpcMessageHandlerRegister {

    @Override
    public void register(RpcMessageHandlerRegistry registry) {
        registry.register(new DefaultRpcMessageHandlerRegistrationInfo(RpcMessageType.REQUEST_EXECUTABLE, HandleSide.SERVER, new RpcExecutableRequestHandler()));
        registry.register(new DefaultRpcMessageHandlerRegistrationInfo(RpcMessageType.RESPONSE_EXECUTABLE, HandleSide.CLIENT, new RpcExecutableResponseHandler()));
    }
}
