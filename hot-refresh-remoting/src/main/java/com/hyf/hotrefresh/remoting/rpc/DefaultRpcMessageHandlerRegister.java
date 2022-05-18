package com.hyf.hotrefresh.remoting.rpc;

import com.hyf.hotrefresh.remoting.rpc.enums.HandleSide;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.handler.*;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class DefaultRpcMessageHandlerRegister implements RpcMessageHandlerRegister {

    @Override
    public void register(RpcMessageHandlerRegistry registry) {
        registry.register(new DefaultRpcMessageHandlerRegistrationInfo(RpcMessageType.REQUEST_BASIC, HandleSide.SERVER, new RpcRequestHandler()));
        registry.register(new DefaultRpcMessageHandlerRegistrationInfo(RpcMessageType.RESPONSE_BASIC, HandleSide.CLIENT, new RpcResponseHandler()));
        registry.register(new DefaultRpcMessageHandlerRegistrationInfo(RpcMessageType.REQUEST_BATCH, HandleSide.SERVER, new RpcBatchRequestHandler()));
        registry.register(new DefaultRpcMessageHandlerRegistrationInfo(RpcMessageType.RESPONSE_BATCH, HandleSide.CLIENT, new RpcBatchResponseHandler()));
        registry.register(new DefaultRpcMessageHandlerRegistrationInfo(RpcMessageType.REQUEST_HEARTBEAT, HandleSide.SERVER, new RpcHeartbeatRequestHandler()));
        registry.register(new DefaultRpcMessageHandlerRegistrationInfo(RpcMessageType.RESPONSE_HEARTBEAT, HandleSide.CLIENT, new RpcHeartbeatResponseHandler()));
        registry.register(new DefaultRpcMessageHandlerRegistrationInfo(RpcMessageType.RESPONSE_SUCCESS, HandleSide.CLIENT, new RpcResponseHandler()));
        registry.register(new DefaultRpcMessageHandlerRegistrationInfo(RpcMessageType.RESPONSE_ERROR, HandleSide.CLIENT, new RpcErrorResponseHandler()));
    }
}
