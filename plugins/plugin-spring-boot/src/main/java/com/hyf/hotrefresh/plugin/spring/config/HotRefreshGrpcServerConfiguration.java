package com.hyf.hotrefresh.plugin.spring.config;

import com.hyf.hotrefresh.plugin.grpc.server.GrpcServer;
import com.hyf.hotrefresh.plugin.grpc.server.GrpcServerConfig;
import com.hyf.hotrefresh.plugin.spring.properties.GrpcServerProperties;
import com.hyf.hotrefresh.plugin.spring.properties.HotRefreshProperties;
import com.hyf.hotrefresh.remoting.server.RpcServer;
import io.grpc.Grpc;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author baB_hyf
 * @date 2022/10/01
 */
@Configuration
@ConditionalOnClass({RpcServer.class, GrpcServer.class, Grpc.class})
@ConditionalOnProperty(prefix = HotRefreshProperties.PREFIX, name = "server.enabled", matchIfMissing = true)
@AutoConfigureBefore(HotRefreshEmbeddedRpcServerConfiguration.class)
@EnableConfigurationProperties(GrpcServerProperties.class)
public class HotRefreshGrpcServerConfiguration {

    @Autowired
    private GrpcServerProperties properties;

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean(name = "hotrefreshGrpcServer")
    public GrpcServer hotrefreshGrpcServer(ObjectProvider<GrpcServerConfig> grpcServerConfigProvider) {
        GrpcServerConfig grpcServerConfig = grpcServerConfigProvider.getIfAvailable();
        if (grpcServerConfig != null) {
            return new GrpcServer(grpcServerConfig);
        } else {
            return new GrpcServer(createDefaultConfig());
        }
    }

    private GrpcServerConfig createDefaultConfig() {
        GrpcServerConfig grpcServerConfig = new GrpcServerConfig();
        grpcServerConfig.setListenPort(properties.getListenPort());
        grpcServerConfig.setMaxInboundMessageSize(properties.getMaxInboundMessageSize());
        grpcServerConfig.setThreadPoolSize(properties.getThreadPoolSize());
        grpcServerConfig.setThreadPoolQueueSize(properties.getThreadPoolQueueSize());
        return grpcServerConfig;
    }
}
