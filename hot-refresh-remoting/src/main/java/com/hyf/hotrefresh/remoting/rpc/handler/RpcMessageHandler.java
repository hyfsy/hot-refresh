package com.hyf.hotrefresh.remoting.rpc.handler;

import com.hyf.hotrefresh.remoting.rpc.RpcMessage;

/**
 * @author baB_hyf
 * @date 2022/05/15
 * @see com.hyf.hotrefresh.remoting.message.handler.MessageHandler
 * @see com.hyf.hotrefresh.remoting.message.handler.AbstractMessageHandler
 */
public interface RpcMessageHandler<REQ extends RpcMessage, RESP extends RpcMessage> {

    Class<REQ> getRpcMessageClassType();

    REQ createEmptyRpcMessage();

    RESP handle(REQ rpcMessage) throws Exception;
}
