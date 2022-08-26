package com.hyf.hotrefresh.plugin.spring.config;

import com.hyf.hotrefresh.plugin.netty.server.NettyRpcServer;
import com.hyf.hotrefresh.plugin.netty.server.NettyServerConfig;
import com.hyf.hotrefresh.plugin.spring.properties.HotRefreshProperties;
import com.hyf.hotrefresh.remoting.server.RpcServer;
import com.hyf.hotrefresh.remoting.server.embedded.EmbeddedRpcServer;
import com.hyf.hotrefresh.remoting.server.embedded.EmbeddedServerConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author baB_hyf
 * @date 2022/08/18
 */
@ConditionalOnClass(RpcServer.class)
public class HotRefreshRpcServerConfiguration {

    @Configuration
    @ConditionalOnClass(NettyRpcServer.class)
    @ConditionalOnProperty(prefix = HotRefreshProperties.PREFIX, name = "server.enabled", matchIfMissing = true)
    static class HotRefreshNettyRpcServerConfiguration {

        @Resource
        private HotRefreshProperties properties;

        @Bean(initMethod = "start", destroyMethod = "stop")
        @ConditionalOnMissingBean(name = "hotrefreshNettyRpcServer")
        public NettyRpcServer hotrefreshNettyRpcServer(ObjectProvider<NettyServerConfig> nettyServerConfigProvider) {
            NettyServerConfig nettyServerConfig = nettyServerConfigProvider.getIfAvailable();
            if (nettyServerConfig != null) {
                return new NettyRpcServer(nettyServerConfig);
            }
            else {
                return new NettyRpcServer(createDefaultConfig());
            }
        }

        private NettyServerConfig createDefaultConfig() {
            NettyServerConfig nettyServerConfig = new NettyServerConfig();
            HotRefreshProperties.Server server = properties.getServer();
            nettyServerConfig.setServerBossThreads(server.getServerBossThreads());
            nettyServerConfig.setServerWorkerThreads(server.getServerWorkerThreads());
            nettyServerConfig.setSoBackLogSize(server.getSoBackLogSize());
            nettyServerConfig.setServerSocketSndBufSize(server.getServerSocketSndBufSize());
            nettyServerConfig.setServerSocketRcvBufSize(server.getServerSocketRcvBufSize());
            nettyServerConfig.setListenPort(server.getListenPort());
            nettyServerConfig.setServerChannelMaxIdleTimeSeconds(server.getServerChannelMaxIdleTimeSeconds());
            nettyServerConfig.setServerPooledByteBufAllocatorEnable(server.isServerPooledByteBufAllocatorEnable());
            nettyServerConfig.setUseEpollNativeSelector(server.isUseEpollNativeSelector());
            return nettyServerConfig;
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = HotRefreshProperties.PREFIX, name = "server.enabled")
    static class HotRefreshEmbeddedRpcServerConfiguration {

        @Resource
        private HotRefreshProperties properties;

        @Bean(initMethod = "start", destroyMethod = "stop")
        @ConditionalOnMissingBean(RpcServer.class)
        public EmbeddedRpcServer hotrefreshEmbeddedRpcServer(ObjectProvider<EmbeddedServerConfig> embeddedServerConfigProvider) {
            EmbeddedServerConfig embeddedServerConfig = embeddedServerConfigProvider.getIfAvailable();
            if (embeddedServerConfig != null) {
                return new EmbeddedRpcServer(embeddedServerConfig);
            }
            else {
                return new EmbeddedRpcServer(createDefaultConfig());
            }
        }

        private EmbeddedServerConfig createDefaultConfig() {
            HotRefreshProperties.Server server = properties.getServer();

            EmbeddedServerConfig embeddedServerConfig = new EmbeddedServerConfig();
            embeddedServerConfig.setServerBossThreads(server.getServerBossThreads());
            embeddedServerConfig.setServerWorkerThreads(server.getServerWorkerThreads());
            embeddedServerConfig.setSoBackLogSize(server.getSoBackLogSize());
            embeddedServerConfig.setServerSocketSndBufSize(server.getServerSocketSndBufSize());
            embeddedServerConfig.setServerSocketRcvBufSize(server.getServerSocketRcvBufSize());
            embeddedServerConfig.setListenPort(server.getListenPort());
            return embeddedServerConfig;
        }
    }
}
