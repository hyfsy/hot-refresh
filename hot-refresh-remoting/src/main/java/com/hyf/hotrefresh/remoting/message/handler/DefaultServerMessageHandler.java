package com.hyf.hotrefresh.remoting.message.handler;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.handler.RpcBatchRequestHandler;
import com.hyf.hotrefresh.remoting.rpc.handler.RpcRequestHandler;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class DefaultServerMessageHandler extends AbstractMessageHandler {

    @Override
    protected void initHandler() {
        addHandler(RpcMessageType.REQUEST.getCode(), new RpcRequestHandler());
        addHandler(RpcMessageType.BATCH_REQUEST.getCode(), new RpcBatchRequestHandler());
    }
}
