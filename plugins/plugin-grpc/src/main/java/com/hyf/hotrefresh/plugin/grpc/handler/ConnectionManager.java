package com.hyf.hotrefresh.plugin.grpc.handler;

import com.google.common.util.concurrent.ListenableFuture;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.NamedThreadFactory;
import com.hyf.hotrefresh.common.hook.Disposable;
import com.hyf.hotrefresh.plugin.grpc.client.ClientMessageStreamObserver;
import com.hyf.hotrefresh.plugin.grpc.client.GrpcClient;
import com.hyf.hotrefresh.plugin.grpc.client.GrpcClientConfig;
import com.hyf.hotrefresh.plugin.grpc.generate.Message;
import com.hyf.hotrefresh.plugin.grpc.generate.RemotingApiGrpc;
import com.hyf.hotrefresh.plugin.grpc.utils.MessageUtils;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcHeartbeatRequest;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcHeartbeatResponse;
import com.hyf.hotrefresh.remoting.util.RemotingUtils;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author baB_hyf
 * @date 2022/10/01
 */
public class ConnectionManager implements Disposable {

    private final Map<String, ConnectionWrapper> connectionTables = new ConcurrentHashMap<>();

    private final Object CONNECTION_LOCK = new Object();

    private final GrpcClient grpcClient;

    private final ExecutorService heartbeatExecutor;

    public ConnectionManager(GrpcClient grpcClient) {
        this.grpcClient = grpcClient;
        this.heartbeatExecutor = Executors.newSingleThreadExecutor(
                new NamedThreadFactory("hotrefresh-grpc-connection-heartbeat-executor", 1));
        this.heartbeatExecutor.submit(() -> {
            while (!grpcClient.stopped()) {
                try {
                    healthCheck();
                } catch (Throwable t) {
                    Log.error("Failed to process health check", t);
                }
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException ignored) {
                }
            }
        });
    }

    public ConnectionWrapper getOrCreateConnection(String addr) {
        ConnectionWrapper connectionWrapper = connectionTables.get(addr);
        if (connectionWrapper == null) {
            synchronized (CONNECTION_LOCK) {
                connectionWrapper = connectionTables.get(addr);
                if (connectionWrapper == null) {
                    connectionWrapper = createConnection(addr);
                    connectionTables.put(addr, connectionWrapper);
                }
            }
        }
        return connectionWrapper;
    }

    private ConnectionWrapper createConnection(String addr) {
        ManagedChannel managedChannel = createManagedChannel(addr);
        RemotingApiGrpc.RemotingApiFutureStub futureStub = createFutureStub(managedChannel);
        StreamObserver<Message> streamObserver = createStreamObserver(managedChannel);
        return new ConnectionWrapper(addr, managedChannel, futureStub, streamObserver);
    }

    private ManagedChannel createManagedChannel(String addr) {
        SocketAddress socketAddress = RemotingUtils.parseSocketAddress(addr);
        if (!(socketAddress instanceof InetSocketAddress)) {
            throw new IllegalArgumentException("Illegal address: " + addr);
        }
        InetSocketAddress address = (InetSocketAddress) socketAddress;
        GrpcClientConfig grpcClientConfig = grpcClient.getGrpcClientConfig();
        return ManagedChannelBuilder
                .forAddress(address.getHostName(), address.getPort())
                .maxInboundMessageSize(grpcClientConfig.getMaxInboundMessageSize())
                .keepAliveTime(grpcClientConfig.getKeepAliveTimeMillis(), TimeUnit.MILLISECONDS)
                .usePlaintext()
                .build();
    }

    private RemotingApiGrpc.RemotingApiFutureStub createFutureStub(ManagedChannel managedChannel) {
        return RemotingApiGrpc.newFutureStub(managedChannel);
    }

    private StreamObserver<Message> createStreamObserver(ManagedChannel managedChannel) {
        RemotingApiGrpc.RemotingApiStub stub = RemotingApiGrpc.newStub(managedChannel);
        return stub.biStream(new ClientMessageStreamObserver());
    }

    private void healthCheck() {

        if (connectionTables.isEmpty()) {
            return;
        }

        RpcHeartbeatRequest request = new RpcHeartbeatRequest();
        com.hyf.hotrefresh.remoting.message.Message message = MessageFactory.createMessage(request);
        Message reqMsg = MessageUtils.convert(message);

        Iterator<ConnectionWrapper> it = connectionTables.values().iterator();
        while (it.hasNext()) {
            ConnectionWrapper connection = it.next();

            boolean health = false;
            try {
                ListenableFuture<Message> responseFuture = connection.getFutureStub().request(reqMsg);
                Message response = responseFuture.get(3000L, TimeUnit.MILLISECONDS);
                com.hyf.hotrefresh.remoting.message.Message msg = MessageUtils.convert(response);
                health = msg != null && (msg.getBody() instanceof RpcHeartbeatResponse);
            } catch (Exception ignored) {
            }

            if (!health) {
                it.remove();
            }
        }
    }

    public void closeConnection(ConnectionWrapper connection) {
        if (connection == null) {
            return;
        }
        connectionTables.remove(connection.getAddr());
        connection.close();
    }

    @Override
    public void destroy() throws Exception {
        heartbeatExecutor.shutdown();
    }
}
