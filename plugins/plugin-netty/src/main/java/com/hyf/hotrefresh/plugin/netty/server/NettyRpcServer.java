package com.hyf.hotrefresh.plugin.netty.server;

import com.hyf.hotrefresh.common.NamedThreadFactory;
import com.hyf.hotrefresh.plugin.netty.handler.ConnectionHandler;
import com.hyf.hotrefresh.plugin.netty.handler.NettyDecoder;
import com.hyf.hotrefresh.plugin.netty.handler.NettyEncoder;
import com.hyf.hotrefresh.plugin.netty.handler.ServerHandler;
import com.hyf.hotrefresh.remoting.MessageCallback;
import com.hyf.hotrefresh.remoting.exception.RemotingException;
import com.hyf.hotrefresh.remoting.exception.ServerException;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.server.DefaultRpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;

/**
 * @author baB_hyf
 * @date 2022/08/16
 */
public class NettyRpcServer extends DefaultRpcServer {

    private final NettyServerConfig nettyServerConfig;
    private final ServerBootstrap   serverBootstrap;
    private final EventLoopGroup    eventLoopGroupBoss;
    private final EventLoopGroup    eventLoopGroupWorker;
    private       int               port;

    // sharable handlers
    private NettyEncoder      nettyEncoder;
    private ConnectionHandler connectionHandler;
    private ServerHandler     serverHandler;

    public NettyRpcServer() {
        this(new NettyServerConfig());
    }

    public NettyRpcServer(NettyServerConfig nettyServerConfig) {
        super();

        this.nettyServerConfig = nettyServerConfig;
        this.serverBootstrap = new ServerBootstrap();

        if (useEpoll()) {
            this.eventLoopGroupBoss = new EpollEventLoopGroup(nettyServerConfig.getServerBossThreads(),
                    new NamedThreadFactory("NettyServerBossExecutor", nettyServerConfig.getServerBossThreads()));
            this.eventLoopGroupWorker = new EpollEventLoopGroup(nettyServerConfig.getServerWorkerThreads(),
                    new NamedThreadFactory("NettyServerWorkerExecutor", nettyServerConfig.getServerWorkerThreads()));
        }
        else {
            this.eventLoopGroupBoss = new NioEventLoopGroup(nettyServerConfig.getServerBossThreads(),
                    new NamedThreadFactory("NettyServerBossExecutor", nettyServerConfig.getServerBossThreads()));
            this.eventLoopGroupWorker = new NioEventLoopGroup(nettyServerConfig.getServerWorkerThreads(),
                    new NamedThreadFactory("NettyServerWorkerExecutor", nettyServerConfig.getServerWorkerThreads()));
        }
    }

    @Override
    public void start() throws ServerException {
        super.start();

        this.nettyEncoder = new NettyEncoder();
        this.connectionHandler = new ConnectionHandler();
        this.serverHandler = new ServerHandler(this);

        ServerBootstrap childHandler = this.serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupWorker)
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, nettyServerConfig.getSoBackLogSize())
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_SNDBUF, nettyServerConfig.getServerSocketSndBufSize())
                .childOption(ChannelOption.SO_RCVBUF, nettyServerConfig.getServerSocketRcvBufSize())
                .localAddress(new InetSocketAddress(nettyServerConfig.getListenPort()))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(nettyEncoder)
                                .addLast(new NettyDecoder())
                                .addLast(new IdleStateHandler(0, 0, nettyServerConfig.getServerChannelMaxIdleTimeSeconds()))
                                .addLast(connectionHandler)
                                .addLast(serverHandler);
                    }
                });

        if (nettyServerConfig.isServerPooledByteBufAllocatorEnable()) {
            childHandler.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        }

        try {
            ChannelFuture sync = this.serverBootstrap.bind().sync();
            InetSocketAddress addr = (InetSocketAddress) sync.channel().localAddress();
            this.port = addr.getPort();
        } catch (InterruptedException e) {
            throw new ServerException("Netty server start bind with InterruptedException", e);
        }
    }

    @Override
    public Message request(String addr, Message message, long timeoutMillis) throws RemotingException {
        // TODO
        return super.request(addr, message, timeoutMillis);
    }

    @Override
    public void requestAsync(String addr, Message message, long timeoutMillis, MessageCallback callback) throws RemotingException {
        // TODO
        super.requestAsync(addr, message, timeoutMillis, callback);
    }

    @Override
    public void stop() throws ServerException {
        super.stop();
        this.eventLoopGroupBoss.shutdownGracefully();
        this.eventLoopGroupWorker.shutdownGracefully();
    }

    public int getPort() {
        return this.port;
    }

    private boolean useEpoll() {
        return System.getProperty("os.name").toLowerCase().contains("linux")
                && Epoll.isAvailable() && nettyServerConfig.isUseEpollNativeSelector();
    }
}
