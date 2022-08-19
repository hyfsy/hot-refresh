package com.hyf.hotrefresh.plugin.netty.client;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.hook.Disposable;
import com.hyf.hotrefresh.remoting.util.RemotingUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author baB_hyf
 * @date 2022/08/16
 */
public class ConnectionManager implements Disposable {

    private final Bootstrap         bootstrap;
    private final NettyClientConfig nettyClientConfig;

    private final Map<String, ChannelWrapper> channelTables = new ConcurrentHashMap<>();

    private final Object CHANNEL_LOCK = new Object();

    public ConnectionManager(Bootstrap bootstrap, NettyClientConfig nettyClientConfig) {
        this.bootstrap = bootstrap;
        this.nettyClientConfig = nettyClientConfig;
    }

    public static void closeChannel(Channel channel) {
        if (channel == null) {
            return;
        }

        String address = RemotingUtils.parseAddress(channel.remoteAddress());
        channel.close().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (Log.isDebugMode()) {
                    Log.info("Channel close, address: " + address);
                }
            }
        });
    }

    /**
     * 获取或创建连接通道
     *
     * @param addr ip:port
     * @return 返回通道，可能为null，表示连接未结束或超时
     */
    public Channel getOrCreateChannel(String addr) {

        ChannelWrapper wrapper = channelTables.get(addr);
        if (wrapper == null || !wrapper.isActive()) {
            return createChannel(addr);
        }

        return wrapper.getChannel();
    }

    public void closeChannel(String addr, Channel channel) {
        if (channel == null) {
            return;
        }

        String address = addr != null ? addr : RemotingUtils.parseAddress(channel.remoteAddress());

        synchronized (CHANNEL_LOCK) {

            boolean remove = true;

            ChannelWrapper wrapper = channelTables.get(address);
            if (wrapper == null) {
                remove = false; // has been removed
            }
            else if (wrapper.getChannel() != channel) {
                remove = false; // has been create new channel
            }

            if (remove) {
                channelTables.remove(address);
            }

            closeChannel(channel);
        }
    }

    @Override
    public void destroy() throws Exception {
        for (ChannelWrapper wrapper : channelTables.values()) {
            closeChannel(null, wrapper.getChannel());
        }
        channelTables.clear();
    }

    private Channel createChannel(String addr) {
        ChannelWrapper wrapper;
        synchronized (CHANNEL_LOCK) {
            wrapper = channelTables.get(addr);
            if (wrapper != null && wrapper.isActive()) {
                return wrapper.getChannel();
            }

            boolean createNew = false;
            if (wrapper == null) {
                createNew = true;
            }
            else if (wrapper.isActive()) {
                return wrapper.getChannel();
            }
            else if (!wrapper.getChannelFuture().isDone()) {
                // not connect complete
            }
            else {
                channelTables.remove(addr);
                createNew = true;
            }

            if (createNew) {
                SocketAddress socketAddress = RemotingUtils.parseSocketAddress(addr);
                ChannelFuture channelFuture = bootstrap.connect(socketAddress);
                wrapper = new ChannelWrapper(channelFuture);
                channelTables.put(addr, wrapper);
            }
        }

        if (wrapper.getChannelFuture().awaitUninterruptibly(nettyClientConfig.getConnectTimeoutMillis())) {
            if (wrapper.isActive()) {
                if (Log.isDebugMode()) {
                    Log.info("Create channel success, connect to " + addr);
                }
                return wrapper.getChannel();
            }
            else {
                Log.warn("Create channel failed: " + addr);
            }
        }
        else {
            Log.warn("Create channel failed, connect to remote address timeout: " + addr);
        }

        return null;
    }

    private static class ChannelWrapper {

        private ChannelFuture channelFuture;

        public ChannelWrapper(ChannelFuture channelFuture) {
            this.channelFuture = channelFuture;
        }

        public boolean isActive() {
            return channelFuture.channel() != null && channelFuture.channel().isActive();
        }

        public ChannelFuture getChannelFuture() {
            return channelFuture;
        }

        public Channel getChannel() {
            return channelFuture.channel();
        }
    }
}
