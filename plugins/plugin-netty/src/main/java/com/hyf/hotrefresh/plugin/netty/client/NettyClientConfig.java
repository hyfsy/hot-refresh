package com.hyf.hotrefresh.plugin.netty.client;

import com.hyf.hotrefresh.common.Constants;

/**
 * @author baB_hyf
 * @date 2022/08/16
 */
public class NettyClientConfig {

    public static final String NETTY_PROPERTIES_PREFIX = Constants.PROPERTIES_PREFIX + ".netty";

    private int clientSelectorThreadSize         = Integer.parseInt(System.getProperty(
            NETTY_PROPERTIES_PREFIX + ".clientSelectorThreadSize", String.valueOf(1)));
    private int connectTimeoutMillis             = Integer.parseInt(System.getProperty(
            NETTY_PROPERTIES_PREFIX + ".connectTimeoutMillis", String.valueOf(3000)));
    private int clientSocketSndBufSize           = Integer.parseInt(System.getProperty(
            NETTY_PROPERTIES_PREFIX + ".clientSocketSndBufSize", String.valueOf(65535)));
    private int clientSocketRcvBufSize           = Integer.parseInt(System.getProperty(
            NETTY_PROPERTIES_PREFIX + ".clientSocketRcvBufSize", String.valueOf(65535)));
    private int clientChannelMaxReadTimeSeconds  = Integer.parseInt(System.getProperty(
            NETTY_PROPERTIES_PREFIX + ".clientChannelMaxReadTimeSeconds", String.valueOf(15)));
    private int clientChannelMaxWriteTimeSeconds = Integer.parseInt(System.getProperty(
            NETTY_PROPERTIES_PREFIX + ".clientChannelMaxWriteTimeSeconds", String.valueOf(5)));
    private int clientChannelMaxIdleTimeSeconds  = Integer.parseInt(System.getProperty(
            NETTY_PROPERTIES_PREFIX + ".clientChannelMaxIdleTimeSeconds", String.valueOf(0)));

    public int getClientSelectorThreadSize() {
        return clientSelectorThreadSize;
    }

    public void setClientSelectorThreadSize(int clientSelectorThreadSize) {
        this.clientSelectorThreadSize = clientSelectorThreadSize;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public int getClientSocketSndBufSize() {
        return clientSocketSndBufSize;
    }

    public void setClientSocketSndBufSize(int clientSocketSndBufSize) {
        this.clientSocketSndBufSize = clientSocketSndBufSize;
    }

    public int getClientSocketRcvBufSize() {
        return clientSocketRcvBufSize;
    }

    public void setClientSocketRcvBufSize(int clientSocketRcvBufSize) {
        this.clientSocketRcvBufSize = clientSocketRcvBufSize;
    }

    public int getClientChannelMaxReadTimeSeconds() {
        return clientChannelMaxReadTimeSeconds;
    }

    public void setClientChannelMaxReadTimeSeconds(int clientChannelMaxReadTimeSeconds) {
        this.clientChannelMaxReadTimeSeconds = clientChannelMaxReadTimeSeconds;
    }

    public int getClientChannelMaxWriteTimeSeconds() {
        return clientChannelMaxWriteTimeSeconds;
    }

    public void setClientChannelMaxWriteTimeSeconds(int clientChannelMaxWriteTimeSeconds) {
        this.clientChannelMaxWriteTimeSeconds = clientChannelMaxWriteTimeSeconds;
    }

    public int getClientChannelMaxIdleTimeSeconds() {
        return clientChannelMaxIdleTimeSeconds;
    }

    public void setClientChannelMaxIdleTimeSeconds(int clientChannelMaxIdleTimeSeconds) {
        this.clientChannelMaxIdleTimeSeconds = clientChannelMaxIdleTimeSeconds;
    }
}
