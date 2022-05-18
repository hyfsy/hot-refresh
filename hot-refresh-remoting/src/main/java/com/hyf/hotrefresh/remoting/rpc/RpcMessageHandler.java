package com.hyf.hotrefresh.remoting.rpc;

import com.hyf.hotrefresh.remoting.constants.RemotingConstants;

/**
 * @author baB_hyf
 * @date 2022/05/15
 * @see com.hyf.hotrefresh.remoting.message.handler.MessageHandler
 * @see com.hyf.hotrefresh.remoting.message.handler.AbstractMessageHandler
 */
public interface RpcMessageHandler<REQ extends RpcMessage, RESP extends RpcMessage> {

    int SUCCESS = RemotingConstants.RESPONSE_SUCCESS;
    int ERROR   = RemotingConstants.RESPONSE_ERROR;
    int UNKNOWN = RemotingConstants.RESPONSE_UNKNOWN;

    default void init(RpcMessageHandlerRegistry registry) {
    }

    RESP handle(REQ rpcMessage) throws Exception;
}
