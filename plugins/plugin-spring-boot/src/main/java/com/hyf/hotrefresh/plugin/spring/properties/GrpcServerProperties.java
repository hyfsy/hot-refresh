package com.hyf.hotrefresh.plugin.spring.properties;

import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author baB_hyf
 * @date 2022/10/01
 */
@ConfigurationProperties(HotRefreshProperties.PREFIX + ".server.grpc")
public class GrpcServerProperties {

    private int listenPort            = RemotingConstants.DEFAULT_RPC_PORT;
    private int maxInboundMessageSize = 10 * 1024 * 1024;
    private int threadPoolSize        = Runtime.getRuntime().availableProcessors() * 4;
    private int threadPoolQueueSize   = 1 << 14;

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public int getMaxInboundMessageSize() {
        return maxInboundMessageSize;
    }

    public void setMaxInboundMessageSize(int maxInboundMessageSize) {
        this.maxInboundMessageSize = maxInboundMessageSize;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public int getThreadPoolQueueSize() {
        return threadPoolQueueSize;
    }

    public void setThreadPoolQueueSize(int threadPoolQueueSize) {
        this.threadPoolQueueSize = threadPoolQueueSize;
    }
}
