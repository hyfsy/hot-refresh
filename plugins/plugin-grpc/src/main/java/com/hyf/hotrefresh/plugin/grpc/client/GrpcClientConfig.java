package com.hyf.hotrefresh.plugin.grpc.client;

import com.hyf.hotrefresh.common.Constants;

/**
 * @author baB_hyf
 * @date 2022/10/01
 */
public class GrpcClientConfig {

    public static final String GRPC_PROPERTIES_PREFIX = Constants.PROPERTIES_PREFIX + ".grpc";

    private int threadPoolSize        = Integer.parseInt(System.getProperty(
            GRPC_PROPERTIES_PREFIX + ".threadPoolSize", String.valueOf(Runtime.getRuntime().availableProcessors() * 4)));
    private int maxInboundMessageSize = Integer.parseInt(System.getProperty(
            GRPC_PROPERTIES_PREFIX + ".maxInboundMessageSize", String.valueOf(10 * 1024 * 1024)));
    private int keepAliveTimeMillis   = Integer.parseInt(System.getProperty(
            GRPC_PROPERTIES_PREFIX + ".keepAliveTimeMillis", String.valueOf(6 * 60 * 1000)));

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public int getMaxInboundMessageSize() {
        return maxInboundMessageSize;
    }

    public void setMaxInboundMessageSize(int maxInboundMessageSize) {
        this.maxInboundMessageSize = maxInboundMessageSize;
    }

    public int getKeepAliveTimeMillis() {
        return keepAliveTimeMillis;
    }

    public void setKeepAliveTimeMillis(int keepAliveTimeMillis) {
        this.keepAliveTimeMillis = keepAliveTimeMillis;
    }
}
