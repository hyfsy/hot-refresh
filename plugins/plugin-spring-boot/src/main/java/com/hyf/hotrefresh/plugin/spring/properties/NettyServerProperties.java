package com.hyf.hotrefresh.plugin.spring.properties;

import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author baB_hyf
 * @date 2022/10/01
 */
@ConfigurationProperties(HotRefreshProperties.PREFIX + ".server.netty")
public class NettyServerProperties {

    private int     serverBossThreads                  = 1;
    private int     serverWorkerThreads                = Runtime.getRuntime().availableProcessors() * 2;
    private int     soBackLogSize                      = 1024;
    private int     serverSocketSndBufSize             = 65535;
    private int     serverSocketRcvBufSize             = 65535;
    private int     listenPort                         = RemotingConstants.DEFAULT_RPC_PORT;
    private int     serverChannelMaxIdleTimeSeconds    = 120;
    private boolean serverPooledByteBufAllocatorEnable = true;
    private boolean useEpollNativeSelector             = true;

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
