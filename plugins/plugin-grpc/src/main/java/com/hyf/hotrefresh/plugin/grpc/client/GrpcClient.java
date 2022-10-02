package com.hyf.hotrefresh.plugin.grpc.client;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.NamedThreadFactory;
import com.hyf.hotrefresh.common.hook.ShutdownHook;
import com.hyf.hotrefresh.plugin.grpc.handler.ConnectionManager;
import com.hyf.hotrefresh.plugin.grpc.handler.ConnectionWrapper;
import com.hyf.hotrefresh.plugin.grpc.utils.MessageUtils;
import com.hyf.hotrefresh.remoting.MessageCallback;
import com.hyf.hotrefresh.remoting.client.DefaultRpcClient;
import com.hyf.hotrefresh.remoting.exception.*;
import com.hyf.hotrefresh.remoting.message.Message;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.concurrent.*;

/**
 * @author baB_hyf
 * @date 2022/10/01
 */
public class GrpcClient extends DefaultRpcClient {

    private static final int RETRY_TIMES = 3;

    private final GrpcClientConfig            grpcClientConfig;
    private       ConnectionManager           connectionManager;
    private       ThreadPoolExecutor          responseFutureExecutor;
    private       ScheduledThreadPoolExecutor timeoutCheckExecutor;

    public GrpcClient() {
        this(new GrpcClientConfig());
    }

    public GrpcClient(GrpcClientConfig grpcClientConfig) {
        this.grpcClientConfig = grpcClientConfig;
    }

    @Override
    public void start() throws ClientException {
        responseFutureExecutor = new ThreadPoolExecutor(
                grpcClientConfig.getThreadPoolSize(),
                grpcClientConfig.getThreadPoolSize(),
                10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10000),
                new NamedThreadFactory("hotrefresh-grpc-response-future-callback-executor", grpcClientConfig.getThreadPoolSize()));

        timeoutCheckExecutor = new ScheduledThreadPoolExecutor(1,
                new NamedThreadFactory("hotrefresh-grpc-response-future-timeout-check-executor", 1));

        connectionManager = new ConnectionManager(this);
        ShutdownHook.getInstance().addDisposable(connectionManager);

        super.start();
    }

    @Override
    public void stop() throws ClientException {
        super.stop();

        responseFutureExecutor.shutdown();
        timeoutCheckExecutor.shutdown();
    }

    @Override
    public Message request(String addr, Message message, long timeoutMillis) throws RemotingException {

        int retryTimes = 0;
        long start = System.currentTimeMillis();

        RemotingException ex = null;

        while (retryTimes < RETRY_TIMES && System.currentTimeMillis() < start + timeoutMillis) {
            ConnectionWrapper connection = connectionManager.getOrCreateConnection(addr);

            try {
                customizeMessage(message);

                ListenableFuture<com.hyf.hotrefresh.plugin.grpc.generate.Message> responseFuture = connection.getFutureStub().request(MessageUtils.convert(message));
                try {
                    return MessageUtils.convert(responseFuture.get(timeoutMillis, TimeUnit.MILLISECONDS));
                } catch (InterruptedException e) {
                    throw new RemotingInterruptedException("Response result await interrupted", e);
                } catch (ExecutionException e) {
                    throw new RemotingExecutionException("Response result handle failed", e);
                } catch (TimeoutException e) {
                    throw new RemotingTimeoutException("Response result await timeout");
                }
            } catch (RemotingException e) {
                ex = e;
            } catch (Exception e) {
                ex = new RemotingException("Failed to request", e);
            }

            Log.error("Failed to request server: " + addr, ex);
            connectionManager.closeConnection(connection);
            retryTimes++;
        }

        if (ex != null) {
            throw ex;
        }
        else {
            throw new RemotingException("Cannot happen");
        }
    }

    @Override
    public void requestAsync(String addr, Message message, long timeoutMillis, MessageCallback callback) throws RemotingException {

        ConnectionWrapper connection = connectionManager.getOrCreateConnection(addr);

        customizeMessage(message);

        ListenableFuture<com.hyf.hotrefresh.plugin.grpc.generate.Message> responseFuture = connection.getFutureStub().request(MessageUtils.convert(message));
        Futures.addCallback(responseFuture, new FutureCallback<com.hyf.hotrefresh.plugin.grpc.generate.Message>() {
            @Override
            public void onSuccess(@NullableDecl com.hyf.hotrefresh.plugin.grpc.generate.Message message) {
                Message response = MessageUtils.convert(message);
                if (response != null) {
                    callback.handle(response, null);
                }
                else {
                    connectionManager.closeConnection(connection);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.error("Failed to request", t);
                connectionManager.closeConnection(connection);
            }
        }, responseFutureExecutor);

        Futures.withTimeout(responseFuture, timeoutMillis, TimeUnit.MILLISECONDS,
                timeoutCheckExecutor);
    }

    public GrpcClientConfig getGrpcClientConfig() {
        return grpcClientConfig;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
