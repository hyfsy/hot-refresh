package com.hyf.hotrefresh.plugin.spring.config;

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
 * @date 2022/09/02
 */
@Configuration
@ConditionalOnClass(RpcServer.class)
@ConditionalOnProperty(prefix = HotRefreshProperties.PREFIX, name = "server.enabled")
public class HotRefreshEmbeddedRpcServerConfiguration {

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
