package com.hyf.hotrefresh.remoting.client;

import com.hyf.hotrefresh.common.Services;
import com.hyf.hotrefresh.common.hook.Disposable;
import com.hyf.hotrefresh.common.hook.ShutdownHook;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.remoting.MessageCallback;
import com.hyf.hotrefresh.remoting.MessageCustomizer;
import com.hyf.hotrefresh.remoting.exception.ClientException;
import com.hyf.hotrefresh.remoting.exception.RemotingException;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import com.hyf.hotrefresh.remoting.util.RemotingUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author baB_hyf
 * @date 2022/08/16
 */
public class DefaultRpcClient implements RpcClient, Disposable {

    protected final List<MessageCustomizer> messageCustomizers = new ArrayList<>();

    protected final ResponseFutureManager responseFutureManager = new ResponseFutureManager();

    private volatile boolean stopped = false;

    public DefaultRpcClient() {
        this.messageCustomizers.addAll(Services.gets(MessageCustomizer.class));
    }

    @Override
    public void start() throws ClientException {
        ShutdownHook.getInstance().addDisposable(this);
    }

    @Override
    public void stop() throws ClientException {
        stopped = true;
    }

    @Override
    public Message request(String addr, Message message, long timeoutMillis) throws RemotingException {

        SocketAddress address = RemotingUtils.parseSocketAddress(addr);

        try (SocketChannel sc = SocketChannel.open()) {
            sc.configureBlocking(true);
            Socket socket = sc.socket();
            socket.setSoLinger(false, -1);
            socket.setTcpNoDelay(true);
            socket.setReceiveBufferSize(1024 * 64);
            socket.setSendBufferSize(1024 * 64);
            socket.setSoTimeout((int) timeoutMillis);
            socket.connect(address, (int) timeoutMillis);
            try (BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
                 BufferedInputStream bis = new BufferedInputStream(socket.getInputStream())) {
                customizeMessage(message);
                bos.write(MessageCodec.encode(message));
                bos.flush();
                byte[] bytes = IOUtils.readAsByteArray(bis);
                return MessageCodec.decode(bytes);
            }
        } catch (IOException e) {
            throw new RemotingException("Failed to send request", e);
        }
    }

    @Override
    public void requestAsync(String addr, Message message, long timeoutMillis, MessageCallback callback) throws RemotingException {
        try {
            Message response = request(addr, message, timeoutMillis);
            callback.handle(response, null);
        } catch (RemotingException e) {
            callback.handle(null, e);
        }
    }

    protected void customizeMessage(Message message) {
        for (MessageCustomizer customizer : messageCustomizers) {
            customizer.customize(message);
        }
    }

    @Override
    public void destroy() throws Exception {
        stop();
    }

    public List<MessageCustomizer> getMessageCustomizers() {
        return messageCustomizers;
    }

    public void addMessageCustomizer(MessageCustomizer messageCustomizer) {
        messageCustomizers.add(messageCustomizer);
    }

    public ResponseFutureManager getResponseFutureManager() {
        return responseFutureManager;
    }

    public boolean stopped() {
        return stopped;
    }
}
