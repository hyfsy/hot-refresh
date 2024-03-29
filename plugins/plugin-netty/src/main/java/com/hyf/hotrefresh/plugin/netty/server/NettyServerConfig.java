package com.hyf.hotrefresh.plugin.netty.server;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import io.netty.util.NettyRuntime;

/**
 * @author baB_hyf
 * @date 2022/08/16
 */
public class NettyServerConfig {

    public static final String NETTY_PROPERTIES_PREFIX = Constants.PROPERTIES_PREFIX + ".netty";

    private int     serverBossThreads                  = Integer.parseInt(System.getProperty(
            NETTY_PROPERTIES_PREFIX + ".serverBossThreads", String.valueOf(1)));
    private int     serverWorkerThreads                = Integer.parseInt(System.getProperty(
            NETTY_PROPERTIES_PREFIX + ".serverWorkerThreads", String.valueOf(NettyRuntime.availableProcessors() * 2)));
    private int     soBackLogSize                      = Integer.parseInt(System.getProperty(
            NETTY_PROPERTIES_PREFIX + ".soBackLogSize", String.valueOf(1024)));
    private int     serverSocketSndBufSize             = Integer.parseInt(System.getProperty(
            NETTY_PROPERTIES_PREFIX + ".serverSocketSndBufSize", String.valueOf(65535)));
    private int     serverSocketRcvBufSize             = Integer.parseInt(System.getProperty(
            NETTY_PROPERTIES_PREFIX + ".serverSocketRcvBufSize", String.valueOf(65535)));
    private int     listenPort                         = Integer.parseInt(System.getProperty(
            NETTY_PROPERTIES_PREFIX + ".listenPort", String.valueOf(RemotingConstants.DEFAULT_RPC_PORT)));
    private int     serverChannelMaxIdleTimeSeconds    = Integer.parseInt(System.getProperty(
            NETTY_PROPERTIES_PREFIX + ".serverChannelMaxIdleTimeSeconds", String.valueOf(120)));
    private boolean serverPooledByteBufAllocatorEnable = Boolean.parseBoolean(System.getProperty(
            NETTY_PROPERTIES_PREFIX + ".serverPooledByteBufAllocatorEnable", String.valueOf(true)));
    private boolean useEpollNativeSelector             = Boolean.parseBoolean(System.getProperty(
            NETTY_PROPERTIES_PREFIX + ".useEpollNativeSelector", String.valueOf(true)));

    public int getServerBossThreads() {
        return serverBossThreads;
    }

    public void setServerBossThreads(int serverBossThreads) {
        this.serverBossThreads = serverBossThreads;
    }

    public int getServerWorkerThreads() {
        return serverWorkerThreads;
    }

    public void setServerWorkerThreads(int serverWorkerThreads) {
        this.serverWorkerThreads = serverWorkerThreads;
    }

    public int getSoBackLogSize() {
        return soBackLogSize;
    }

    public void setSoBackLogSize(int soBackLogSize) {
        this.soBackLogSize = soBackLogSize;
    }

    public int getServerSocketSndBufSize() {
        return serverSocketSndBufSize;
    }

    public void setServerSocketSndBufSize(int serverSocketSndBufSize) {
        this.serverSocketSndBufSize = serverSocketSndBufSize;
    }

    public int getServerSocketRcvBufSize() {
        return serverSocketRcvBufSize;
    }

    public void setServerSocketRcvBufSize(int serverSocketRcvBufSize) {
        this.serverSocketRcvBufSize = serverSocketRcvBufSize;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public int getServerChannelMaxIdleTimeSeconds() {
        return serverChannelMaxIdleTimeSeconds;
    }

    public void setServerChannelMaxIdleTimeSeconds(int serverChannelMaxIdleTimeSeconds) {
        this.serverChannelMaxIdleTimeSeconds = serverChannelMaxIdleTimeSeconds;
    }

    public boolean isServerPooledByteBufAllocatorEnable() {
        return serverPooledByteBufAllocatorEnable;
    }

    public void setServerPooledByteBufAllocatorEnable(boolean serverPooledByteBufAllocatorEnable) {
        this.serverPooledByteBufAllocatorEnable = serverPooledByteBufAllocatorEnable;
    }

    public boolean isUseEpollNativeSelector() {
        return useEpollNativeSelector;
    }

    public void setUseEpollNativeSelector(boolean useEpollNativeSelector) {
        this.useEpollNativeSelector = useEpollNativeSelector;
    }
}
