package com.hyf.hotrefresh.plugin.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 热刷新配置
 *
 * @author baB_hyf
 * @date 2022/05/13
 */
@ConfigurationProperties(HotRefreshProperties.PREFIX)
public class HotRefreshProperties {

    public static final String PREFIX = "hyf.hot-refresh";

    /**
     * 针对SpringMVC提供的拦截器的相关配置
     */
    private final InterceptorEscape interceptorEscape = new InterceptorEscape();

    /**
     * 热刷新rpc服务配置
     */
    private final Server server = new Server();

    /**
     * 是否启用热刷新功能
     */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public InterceptorEscape getInterceptorEscape() {
        return interceptorEscape;
    }

    public Server getServer() {
        return server;
    }

    public static class InterceptorEscape {

        /**
         * SpringMVC提供的拦截器是否进行跳过-全局配置
         */
        private boolean enabled = true;

        /**
         * 跳过的类
         */
        private List<Class<?>> includeClasses = new ArrayList<>();

        /**
         * 不跳过的类
         */
        private List<Class<?>> excludeClasses = new ArrayList<>();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<Class<?>> getIncludeClasses() {
            return includeClasses;
        }

        public void setIncludeClasses(List<Class<?>> includeClasses) {
            this.includeClasses = includeClasses;
        }

        public List<Class<?>> getExcludeClasses() {
            return excludeClasses;
        }

        public void setExcludeClasses(List<Class<?>> excludeClasses) {
            this.excludeClasses = excludeClasses;
        }
    }

    public static class Server {

        /**
         * 是否启用内置rpc服务
         */
        private boolean enabled = false;

        // TODO 服务器参数绑定

        private int     serverBossThreads                  = 1;
        private int     serverWorkerThreads                = Runtime.getRuntime().availableProcessors() * 2;
        private int     soBackLogSize                      = 1024;
        private int     serverSocketSndBufSize             = 65535;
        private int     serverSocketRcvBufSize             = 65535;
        private int     listenPort                         = 5946;
        private int     serverChannelMaxIdleTimeSeconds    = 120;
        private boolean serverPooledByteBufAllocatorEnable = true;
        private boolean useEpollNativeSelector             = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

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
}
