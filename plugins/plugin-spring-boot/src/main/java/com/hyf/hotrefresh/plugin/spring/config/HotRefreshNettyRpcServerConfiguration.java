package com.hyf.hotrefresh.plugin.spring.config;

import com.hyf.hotrefresh.plugin.netty.server.NettyRpcServer;
import com.hyf.hotrefresh.plugin.netty.server.NettyServerConfig;
import com.hyf.hotrefresh.plugin.spring.properties.HotRefreshProperties;
import com.hyf.hotrefresh.remoting.server.RpcServer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author baB_hyf
 * @date 2022/09/02
 */
@Configuration
@ConditionalOnClass({RpcServer.class, NettyRpcServer.class})
@ConditionalOnProperty(prefix = HotRefreshProperties.PREFIX, name = "server.enabled", matchIfMissing = true)
@AutoConfigureBefore(HotRefreshEmbeddedRpcServerConfiguration.class)
public class HotRefreshNettyRpcServerConfiguration {

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
