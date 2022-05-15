package com.hyf.hotrefresh.remoting.message.handler;

import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.handler.RpcResponseHandler;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public class DefaultClientMessageHandler extends AbstractMessageHandler {

    @Override
    protected void initHandler() {
        addHandler(RpcMessageType.RESPONSE.getCode(), new RpcResponseHandler());
    }
}
