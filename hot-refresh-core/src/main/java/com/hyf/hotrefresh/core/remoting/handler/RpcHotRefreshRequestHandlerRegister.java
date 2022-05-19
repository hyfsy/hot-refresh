package com.hyf.hotrefresh.core.remoting.handler;

import com.hyf.hotrefresh.remoting.rpc.DefaultRpcMessageHandlerRegistrationInfo;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandlerRegister;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandlerRegistry;
import com.hyf.hotrefresh.remoting.rpc.enums.HandleSide;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;

/**
 * @author baB_hyf
 * @date 2022/05/19
 */
public class RpcHotRefreshRequestHandlerRegister implements RpcMessageHandlerRegister {

    @Override
    public void register(RpcMessageHandlerRegistry registry) {
        registry.register(new DefaultRpcMessageHandlerRegistrationInfo(RpcMessageType.REQUEST_HOT_REFRESH, HandleSide.SERVER, new RpcHotRefreshRequestHandler()));
        registry.register(new DefaultRpcMessageHandlerRegistrationInfo(RpcMessageType.RESPONSE_HOT_REFRESH, HandleSide.CLIENT, new RpcHotRefreshResponseHandler()));
    }
}
