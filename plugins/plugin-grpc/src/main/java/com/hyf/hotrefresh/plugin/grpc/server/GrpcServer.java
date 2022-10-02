package com.hyf.hotrefresh.plugin.grpc.server;

import com.hyf.hotrefresh.common.NamedThreadFactory;
import com.hyf.hotrefresh.remoting.MessageCallback;
import com.hyf.hotrefresh.remoting.exception.RemotingException;
import com.hyf.hotrefresh.remoting.exception.ServerException;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.server.DefaultRpcServer;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author baB_hyf
 * @date 2022/10/01
 */
public class GrpcServer extends DefaultRpcServer {

    private final GrpcServerConfig grpcServerConfig;
    private       Server           server;

    public GrpcServer() {
        this(new GrpcServerConfig());
    }

    public GrpcServer(GrpcServerConfig grpcServerConfig) {
        this.grpcServerConfig = grpcServerConfig;
    }

    @Override
    public void start() throws ServerException {
        super.start();

        ThreadPoolExecutor hotRefreshGrpcExecutor = new ThreadPoolExecutor(
                grpcServerConfig.getThreadPoolSize(),
                grpcServerConfig.getThreadPoolSize(),
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(grpcServerConfig.getThreadPoolQueueSize()),
                new NamedThreadFactory("hotrefresh-grpc-server-executor", grpcServerConfig.getThreadPoolSize()));

        server = ServerBuilder.forPort(grpcServerConfig.getListenPort())
                .executor(hotRefreshGrpcExecutor)
                .maxInboundMessageSize(grpcServerConfig.getMaxInboundMessageSize())
                .addService(new RemotingApiAcceptor(this))
                .build();

        try {
            server.start();
        } catch (IOException e) {
            throw new ServerException("Failed to start grpc server", e);
        }
    }

    @Override
    public void stop() throws ServerException {
        super.stop();

        if (server != null) {
            server.shutdownNow();
        }
    }

    @Override
    public Message request(String addr, Message message, long timeoutMillis) throws RemotingException {
        return super.request(addr, message, timeoutMillis);
    }

    @Override
    public void requestAsync(String addr, Message message, long timeoutMillis, MessageCallback callback) throws RemotingException {
        super.requestAsync(addr, message, timeoutMillis, callback);
    }
}
