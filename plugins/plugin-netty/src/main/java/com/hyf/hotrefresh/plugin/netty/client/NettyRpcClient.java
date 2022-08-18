package com.hyf.hotrefresh.plugin.netty.client;

import com.hyf.hotrefresh.common.NamedThreadFactory;
import com.hyf.hotrefresh.common.hook.ShutdownHook;
import com.hyf.hotrefresh.plugin.netty.handler.ClientHandler;
import com.hyf.hotrefresh.plugin.netty.handler.ConnectionHandler;
import com.hyf.hotrefresh.plugin.netty.handler.NettyDecoder;
import com.hyf.hotrefresh.plugin.netty.handler.NettyEncoder;
import com.hyf.hotrefresh.remoting.MessageCallback;
import com.hyf.hotrefresh.remoting.ResponseFuture;
import com.hyf.hotrefresh.remoting.client.DefaultRpcClient;
import com.hyf.hotrefresh.remoting.exception.ClientException;
import com.hyf.hotrefresh.remoting.exception.RemotingException;
import com.hyf.hotrefresh.remoting.message.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author baB_hyf
 * @date 2022/08/16
 */
public class NettyRpcClient extends DefaultRpcClient {

    private final NettyClientConfig nettyClientConfig;
    private final Bootstrap         bootstrap;
    private final EventLoopGroup    eventLoopGroupWorker;

    private final ConnectionManager connectionManager;

    private final Map<Integer, ResponseFuture> futureTables = new ConcurrentHashMap<>();

    public NettyRpcClient() {
        this(new NettyClientConfig());
    }

    public NettyRpcClient(NettyClientConfig nettyClientConfig) {
        super();

        this.nettyClientConfig = nettyClientConfig;
        this.bootstrap = new Bootstrap();
        this.eventLoopGroupWorker = new NioEventLoopGroup(nettyClientConfig.getClientSelectorThreadSize(),
                new NamedThreadFactory("NettyClientSelector_", nettyClientConfig.getClientSelectorThreadSize()));

        this.connectionManager = new ConnectionManager(this.bootstrap, this.nettyClientConfig);
    }

    @Override
    public void start() throws ClientException {
        super.start();

        this.bootstrap.group(this.eventLoopGroupWorker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyClientConfig.getConnectTimeoutMillis())
                .option(ChannelOption.SO_SNDBUF, nettyClientConfig.getClientSocketSndBufSize())
                .option(ChannelOption.SO_RCVBUF, nettyClientConfig.getClientSocketRcvBufSize())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new NettyEncoder())
                                .addLast(new NettyDecoder())
                                .addLast(new IdleStateHandler(
                                        nettyClientConfig.getClientChannelMaxReadTimeSeconds(),
                                        nettyClientConfig.getClientChannelMaxWriteTimeSeconds(),
                                        nettyClientConfig.getClientChannelMaxIdleTimeSeconds()))
                                .addLast(new ConnectionHandler())
                                .addLast(new ClientHandler(NettyRpcClient.this));
                    }
                });

        ShutdownHook.getInstance().addDisposable(connectionManager);
    }

    @Override
    public Message request(String addr, Message message, long timeoutMillis) throws RemotingException {
        Channel channel = connectionManager.getOrCreateChannel(addr);
        if (channel != null && channel.isActive()) {

            customizeMessage(message);

            ResponseFuture responseFuture = new ResponseFuture();

            futureTables.put(message.getId(), responseFuture);

            channel.writeAndFlush(message).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess()) {
                        responseFuture.fail(channelFuture.cause());
                    }
                }
            });

            return responseFuture.get(timeoutMillis);
        }
        else {
            connectionManager.closeChannel(addr, channel);
            throw new RemotingException("Failed to connect remote address: " + addr);
        }
    }

    @Override
    public void requestAsync(String addr, Message message, long timeoutMillis, MessageCallback callback) throws RemotingException {
        // TODO
        super.requestAsync(addr, message, timeoutMillis, callback);
    }

    @Override
    public void stop() throws ClientException {
        super.stop();

        this.eventLoopGroupWorker.shutdownGracefully();
    }

    public Map<Integer, ResponseFuture> getFutureTables() {
        return futureTables;
    }
}
