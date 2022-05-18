package com.hyf.hotrefresh.remoting.message.handler;

import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandlerRegistry;

/**
 * 方便过早暴露处理器，解决循环依赖问题
 *
 * @author baB_hyf
 * @date 2022/05/15
 */
public class MessageHandlerFactory {

    private static volatile MessageHandler clientMessageHandler;
    private static volatile MessageHandler serverMessageHandler;

    public static synchronized MessageHandler getClientMessageHandler() {
        if (clientMessageHandler != null) {
            return clientMessageHandler;
        }
        DefaultClientMessageHandler messageHandler = new DefaultClientMessageHandler(RpcMessageHandlerRegistry.getInstance());
        clientMessageHandler = messageHandler;
        return messageHandler;
    }

    public static synchronized MessageHandler getServerMessageHandler() {
        if (serverMessageHandler != null) {
            return serverMessageHandler;
        }
        DefaultServerMessageHandler messageHandler = new DefaultServerMessageHandler(RpcMessageHandlerRegistry.getInstance());
        serverMessageHandler = messageHandler;
        return messageHandler;
    }
}
