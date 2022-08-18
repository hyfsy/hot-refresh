package com.hyf.hotrefresh.plugin.spring.config;

import com.hyf.hotrefresh.plugin.netty.server.NettyRpcServer;
import com.hyf.hotrefresh.plugin.netty.server.NettyServerConfig;
import com.hyf.hotrefresh.remoting.server.RpcServer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author baB_hyf
 * @date 2022/08/18
 */
@ConditionalOnClass(RpcServer.class)
public class HotRefreshRpcServerConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnClass(NettyRpcServer.class)
    @ConditionalOnMissingBean
    public NettyRpcServer nettyRpcServer(ObjectProvider<NettyServerConfig> nettyServerConfigProvider) {
        NettyServerConfig nettyServerConfig = nettyServerConfigProvider.getIfAvailable();
        if (nettyServerConfig != null) {
            return new NettyRpcServer(nettyServerConfig);
        }
        else {
            return new NettyRpcServer();
        }
    }

}
