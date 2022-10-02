package com.hyf.hotrefresh.plugin.spring.config;

import com.hyf.hotrefresh.plugin.spring.properties.EmbeddedServerProperties;
import com.hyf.hotrefresh.plugin.spring.properties.HotRefreshProperties;
import com.hyf.hotrefresh.remoting.server.RpcServer;
import com.hyf.hotrefresh.remoting.server.embedded.EmbeddedRpcServer;
import com.hyf.hotrefresh.remoting.server.embedded.EmbeddedServerConfig;
import org.springframework.beans.factory.ObjectProvider;
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
@ConditionalOnClass(RpcServer.class)
@ConditionalOnProperty(prefix = HotRefreshProperties.PREFIX, name = "server.enabled")
@EnableConfigurationProperties(EmbeddedServerProperties.class)
public class HotRefreshEmbeddedRpcServerConfiguration {

    @Resource
    private EmbeddedServerProperties properties;

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean(value = RpcServer.class)
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
        EmbeddedServerConfig embeddedServerConfig = new EmbeddedServerConfig();
        embeddedServerConfig.setServerBossThreads(properties.getServerBossThreads());
        embeddedServerConfig.setServerWorkerThreads(properties.getServerWorkerThreads());
        embeddedServerConfig.setSoBackLogSize(properties.getSoBackLogSize());
        embeddedServerConfig.setServerSocketSndBufSize(properties.getServerSocketSndBufSize());
        embeddedServerConfig.setServerSocketRcvBufSize(properties.getServerSocketRcvBufSize());
        embeddedServerConfig.setListenPort(properties.getListenPort());
        return embeddedServerConfig;
    }
}
