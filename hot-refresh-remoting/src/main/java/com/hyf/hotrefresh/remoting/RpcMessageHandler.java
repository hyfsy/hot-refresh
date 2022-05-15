package com.hyf.hotrefresh.remoting;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public interface RpcMessageHandler {

    Class<?> getRpcMessageClassType();

    RpcMessage handle(RpcMessage rpcMessage) throws Exception;
}
