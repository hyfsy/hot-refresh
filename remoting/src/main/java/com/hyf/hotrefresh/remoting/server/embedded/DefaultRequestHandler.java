package com.hyf.hotrefresh.remoting.server.embedded;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcErrorResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author baB_hyf
 * @date 2022/08/21
 */
public class DefaultRequestHandler implements RequestHandler {

    private final EmbeddedRpcServer embeddedRpcServer;

    public DefaultRequestHandler(EmbeddedRpcServer embeddedRpcServer) {
        this.embeddedRpcServer = embeddedRpcServer;
    }

    @Override
    public ByteBuffer read(SocketChannel sc) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteBuffer buf = ByteBuffer.allocate(2048);
        int len;
        while ((len = sc.read(buf)) != 0) {
            buf.flip();
            baos.write(buf.array(), 0, len);
            buf.clear();
        }
        return ByteBuffer.wrap(baos.toByteArray());
    }

    @Override
    public ByteBuffer handle(ByteBuffer buf) throws Throwable {
        Message request = MessageCodec.decode(buf);
        Message response = embeddedRpcServer.handle(request);
        return ByteBuffer.wrap(MessageCodec.encode(response));
    }

    @Override
    public void write(SocketChannel sc, ByteBuffer buf) throws IOException {
        sc.write(buf);
    }

    @Override
    public void caught(SocketChannel sc, ByteBuffer buf, Throwable t) {
        try {
            RpcErrorResponse rpcErrorResponse = new RpcErrorResponse();
            rpcErrorResponse.setThrowable(t);
            if (buf != null) {
                Message responseMessage = MessageFactory.createResponseMessage(MessageCodec.decode(buf), rpcErrorResponse);
                this.write(sc, MessageCodec.encodeByteBuffer(responseMessage));
            }
            else {
                if (Log.isDebugMode()) {
                    Log.error("Failed to read channel", t);
                }
            }
        } catch (IOException e) {
            if (Log.isDebugMode()) {
                Log.error("Failed to handler exception", e);
            }
        }
    }
}
