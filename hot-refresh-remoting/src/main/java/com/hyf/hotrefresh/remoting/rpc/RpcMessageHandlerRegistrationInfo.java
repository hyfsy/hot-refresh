package com.hyf.hotrefresh.remoting.rpc;

import com.hyf.hotrefresh.remoting.rpc.enums.HandleSide;

/**
 * @author baB_hyf
 * @date 2022/05/17
 */
public interface RpcMessageHandlerRegistrationInfo {

    byte handleMessageCode();

    HandleSide handleSide();

    RpcMessageHandler<? extends RpcMessage, ? extends RpcMessage> rpcMessageHandler();

}
