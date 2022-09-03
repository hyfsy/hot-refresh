package com.hyf.hotrefresh.remoting.server.embedded;

import com.hyf.hotrefresh.common.Constants;

/**
 * @author baB_hyf
 * @date 2022/08/21
 */
public class EmbeddedServerConfig {

    public static final boolean TCP_DEBUG = false;

    public static final String EMBEDDED_PROPERTIES_PREFIX = Constants.PROPERTIES_PREFIX + ".embedded";

    private int serverBossThreads      = Integer.parseInt(System.getProperty(
            EMBEDDED_PROPERTIES_PREFIX + ".serverBossThreads", String.valueOf(1)));
    private int serverWorkerThreads    = Integer.parseInt(System.getProperty(
            EMBEDDED_PROPERTIES_PREFIX + ".serverWorkerThreads", String.valueOf(Runtime.getRuntime().availableProcessors() * 2)));
    private int soBackLogSize          = Integer.parseInt(System.getProperty(
            EMBEDDED_PROPERTIES_PREFIX + ".soBackLogSize", String.valueOf(1024)));
    private int serverSocketSndBufSize = Integer.parseInt(System.getProperty(
            EMBEDDED_PROPERTIES_PREFIX + ".serverSocketSndBufSize", String.valueOf(65535)));
    private int serverSocketRcvBufSize = Integer.parseInt(System.getProperty(
            EMBEDDED_PROPERTIES_PREFIX + ".serverSocketRcvBufSize", String.valueOf(65535)));
    private int listenPort             = Integer.parseInt(System.getProperty(
            EMBEDDED_PROPERTIES_PREFIX + ".listenPort", String.valueOf(5946)));

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
}
