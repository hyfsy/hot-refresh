package com.hyf.hotrefresh.remoting.server;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.core.install.CoreInstaller;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandler;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandlerFactory;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcErrorResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class HotRefreshServer {

    private static final HotRefreshServer INSTANCE = new HotRefreshServer();

    private MessageHandler serverMessageHandler = MessageHandlerFactory.getServerMessageHandler();

    private HotRefreshServer() {
        initServer();
    }

    public static HotRefreshServer getInstance() {
        return INSTANCE;
    }

    private void initServer() {
        CoreInstaller.install();
    }

    public void handle(InputStream is, OutputStream os) {

        if (!CoreInstaller.enable()) {
            return;
        }

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
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ignore) {
                }
            }
        }
    }
}
