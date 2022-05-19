package com.hyf.hotrefresh.remoting.server;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.remoting.exception.ServerException;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandler;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandlerFactory;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcErrorResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ServiceLoader;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class HotRefreshServer implements RpcServer {

    private MessageHandler serverMessageHandler = MessageHandlerFactory.getServerMessageHandler();

    private ServiceLoader<RpcServerLifecycle> lifecycles;

    public HotRefreshServer() {
        lifecycles = ServiceLoader.load(RpcServerLifecycle.class);
    }

    @Override
    public void start() throws ServerException {
        for (RpcServerLifecycle lifecycle : lifecycles) {
            lifecycle.start();
        }
    }

    @Override
    public void handle(InputStream is, OutputStream os) {
        try {
            Message message = MessageCodec.decode(IOUtils.readAsByteArray(is));
            Message rtn = serverMessageHandler.handle(message);
            os.write(MessageCodec.encode(rtn));
            os.flush();
        } catch (Throwable t) {
            if (Log.isDebugMode()) {
                Log.error("Handle message failed", t);
            }
            RpcErrorResponse rpcErrorResponse = new RpcErrorResponse();
            rpcErrorResponse.setThrowable(t);
            Message rtn = MessageFactory.createMessage(rpcErrorResponse);
            try {
                os.write(MessageCodec.encode(rtn));
                os.flush();
            } catch (IOException e) {
                if (Log.isDebugMode()) {
                    Log.error("Output write failed", e);
                }
            }
        } finally {
            IOUtils.close(is, os);
        }
    }

    @Override
    public void stop() throws ServerException {
        for (RpcServerLifecycle lifecycle : lifecycles) {
            lifecycle.stop();
        }
    }
}
