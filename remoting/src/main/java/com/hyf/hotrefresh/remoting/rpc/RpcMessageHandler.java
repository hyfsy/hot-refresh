package com.hyf.hotrefresh.remoting.rpc;

/**
 * @author baB_hyf
 * @date 2022/05/15
 * @see com.hyf.hotrefresh.remoting.message.handler.MessageHandler
 * @see com.hyf.hotrefresh.remoting.message.handler.AbstractMessageHandler
 */
public interface RpcMessageHandler<REQ extends RpcMessage, RESP extends RpcMessage> {

    default void init(RpcMessageHandlerRegistry registry) {
    }

    RESP handle(REQ rpcMessage) throws Exception;
}
