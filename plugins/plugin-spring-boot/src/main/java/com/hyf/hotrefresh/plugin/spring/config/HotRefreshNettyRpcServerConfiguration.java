package com.hyf.hotrefresh.plugin.spring.config;

import com.hyf.hotrefresh.plugin.netty.server.NettyRpcServer;
import com.hyf.hotrefresh.plugin.netty.server.NettyServerConfig;
import com.hyf.hotrefresh.plugin.spring.properties.HotRefreshProperties;
import com.hyf.hotrefresh.plugin.spring.properties.NettyServerProperties;
import com.hyf.hotrefresh.remoting.server.RpcServer;
import io.netty.bootstrap.ServerBootstrap;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author baB_hyf
 * @date 2022/09/02
 */
@Configuration
@ConditionalOnClass({RpcServer.class, NettyRpcServer.class, ServerBootstrap.class})
@ConditionalOnProperty(prefix = HotRefreshProperties.PREFIX, name = "server.enabled", matchIfMissing = true)
@AutoConfigureBefore(HotRefreshEmbeddedRpcServerConfiguration.class)
@EnableConfigurationProperties(NettyServerProperties.class)
public class HotRefreshNettyRpcServerConfiguration {

    @Resource
    private NettyServerProperties properties;

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
        nettyServerConfig.setServerBossThreads(properties.getServerBossThreads());
        nettyServerConfig.setServerWorkerThreads(properties.getServerWorkerThreads());
        nettyServerConfig.setSoBackLogSize(properties.getSoBackLogSize());
        nettyServerConfig.setServerSocketSndBufSize(properties.getServerSocketSndBufSize());
        nettyServerConfig.setServerSocketRcvBufSize(properties.getServerSocketRcvBufSize());
        nettyServerConfig.setListenPort(properties.getListenPort());
        nettyServerConfig.setServerChannelMaxIdleTimeSeconds(properties.getServerChannelMaxIdleTimeSeconds());
        nettyServerConfig.setServerPooledByteBufAllocatorEnable(properties.isServerPooledByteBufAllocatorEnable());
        nettyServerConfig.setUseEpollNativeSelector(properties.isUseEpollNativeSelector());
        return nettyServerConfig;
    }
}
