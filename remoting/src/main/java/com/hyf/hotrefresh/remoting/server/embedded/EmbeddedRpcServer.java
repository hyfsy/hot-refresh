package com.hyf.hotrefresh.remoting.server.embedded;

import com.hyf.hotrefresh.common.NamedThreadFactory;
import com.hyf.hotrefresh.remoting.MessageCallback;
import com.hyf.hotrefresh.remoting.exception.RemotingException;
import com.hyf.hotrefresh.remoting.exception.ServerException;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.server.DefaultRpcServer;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author baB_hyf
 * @date 2022/08/18
 */
public class EmbeddedRpcServer extends DefaultRpcServer {

    private final EmbeddedServerConfig       embeddedServerConfig;
    private final EmbeddedRpcServerBootstrap serverBootstrap;
    private final ExecutorService            embeddedServerBossExecutor;
    private final ExecutorService            embeddedServerWorkerExecutor;

    public EmbeddedRpcServer() {
        this(new EmbeddedServerConfig());
    }

    public EmbeddedRpcServer(EmbeddedServerConfig embeddedServerConfig) {
        this.embeddedServerConfig = embeddedServerConfig;
        this.serverBootstrap = new EmbeddedRpcServerBootstrap();
        this.embeddedServerBossExecutor = Executors.newFixedThreadPool(embeddedServerConfig.getServerBossThreads(),
                new NamedThreadFactory("EmbeddedServerBossExecutor", embeddedServerConfig.getServerBossThreads()));
        this.embeddedServerWorkerExecutor = Executors.newFixedThreadPool(embeddedServerConfig.getServerWorkerThreads(),
                new NamedThreadFactory("EmbeddedServerWorkerExecutor", embeddedServerConfig.getServerWorkerThreads()));
    }

    @Override
    public void start() throws ServerException {
        super.start();

        this.serverBootstrap.group(embeddedServerBossExecutor, embeddedServerWorkerExecutor)
                .backlog(embeddedServerConfig.getSoBackLogSize())
                .option(StandardSocketOptions.SO_REUSEADDR, true)
                .childOption(StandardSocketOptions.SO_KEEPALIVE, true)
                .childOption(StandardSocketOptions.TCP_NODELAY, true)
                .childOption(StandardSocketOptions.SO_SNDBUF, embeddedServerConfig.getServerSocketSndBufSize())
                .childOption(StandardSocketOptions.SO_RCVBUF, embeddedServerConfig.getServerSocketRcvBufSize())
                .localAddress(new InetSocketAddress(embeddedServerConfig.getListenPort()))
                .childHandler(new DefaultRequestHandler(this))
                .bind();
    }

    @Override
    public void stop() throws ServerException {
        super.stop();
        serverBootstrap.shutdownGracefully();
    }

    @Override
    public Message request(String addr, Message message, long timeoutMillis) throws RemotingException {
        return super.request(addr, message, timeoutMillis);
    }

    @Override
    public void requestAsync(String addr, Message message, long timeoutMillis, MessageCallback callback) throws RemotingException {
        super.requestAsync(addr, message, timeoutMillis, callback);
    }
}
