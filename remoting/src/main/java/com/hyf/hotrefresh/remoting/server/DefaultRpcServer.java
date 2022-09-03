package com.hyf.hotrefresh.remoting.server;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.Services;
import com.hyf.hotrefresh.common.hook.Disposable;
import com.hyf.hotrefresh.common.hook.ShutdownHook;
import com.hyf.hotrefresh.remoting.MessageCallback;
import com.hyf.hotrefresh.remoting.exception.ClientException;
import com.hyf.hotrefresh.remoting.exception.RemotingException;
import com.hyf.hotrefresh.remoting.exception.ServerException;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandler;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandlerFactory;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcErrorResponse;

import java.util.List;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class DefaultRpcServer implements RpcServer, Disposable {

    private final MessageHandler           serverMessageHandler;
    private final List<RpcServerLifecycle> lifecycles;

    public DefaultRpcServer() {
        serverMessageHandler = MessageHandlerFactory.getServerMessageHandler();
        lifecycles = Services.gets(RpcServerLifecycle.class);
    }

    @Override
    public void start() throws ServerException {
        for (RpcServerLifecycle lifecycle : lifecycles) {
            lifecycle.start();
        }

        ShutdownHook.getInstance().addDisposable(this);
    }

    @Override
    public void stop() throws ServerException {
        for (RpcServerLifecycle lifecycle : lifecycles) {
            lifecycle.stop();
        }
    }

    @Override
    public Message request(String addr, Message message, long timeoutMillis) throws RemotingException {
        // TODO
        return null;
    }

    @Override
    public void requestAsync(String addr, Message message, long timeoutMillis, MessageCallback callback) throws RemotingException {
        // TODO
        try {
            Message response = request(addr, message, timeoutMillis);
            callback.handle(response, null);
        } catch (ClientException e) {
            callback.handle(null, e);
        }
    }

    // TODO 服务端功能无
    public Message handle(Message message) {
        try {
            return serverMessageHandler.handle(message);
        } catch (Throwable t) {
            if (Log.isDebugMode()) {
                Log.error("Handle message failed", t);
            }
            RpcErrorResponse rpcErrorResponse = new RpcErrorResponse();
            rpcErrorResponse.setThrowable(t);
            return MessageFactory.createResponseMessage(message, rpcErrorResponse);
        }
    }

    @Override
    public void destroy() throws Exception {
        stop();
    }

    public MessageHandler getServerMessageHandler() {
        return this.serverMessageHandler;
    }

    public List<RpcServerLifecycle> getRpcServerLifecycles() {
        return lifecycles;
    }

    public void addRpcServerLifecycle(RpcServerLifecycle rpcServerLifecycle) {
        lifecycles.add(rpcServerLifecycle);
    }
}
