package com.hyf.hotrefresh.plugin.grpc.handler;

import com.hyf.hotrefresh.plugin.grpc.generate.Message;
import com.hyf.hotrefresh.plugin.grpc.generate.RemotingApiGrpc;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

/**
 * @author baB_hyf
 * @date 2022/10/01
 */
public class ConnectionWrapper {

    private final String                                addr;
    private final ManagedChannel                        channel;
    private final RemotingApiGrpc.RemotingApiFutureStub futureStub;
    private final StreamObserver<Message>               streamObserver;

    public ConnectionWrapper(String addr, ManagedChannel channel, RemotingApiGrpc.RemotingApiFutureStub futureStub, StreamObserver<Message> streamObserver) {
        this.addr = addr;
        this.channel = channel;
        this.futureStub = futureStub;
        this.streamObserver = streamObserver;
    }

    public String getAddr() {
        return addr;
    }

    public ManagedChannel getChannel() {
        return channel;
    }

    public RemotingApiGrpc.RemotingApiFutureStub getFutureStub() {
        return futureStub;
    }

    public StreamObserver<Message> getStreamObserver() {
        return streamObserver;
    }

    public void close() {
        if (streamObserver != null) {
            streamObserver.onCompleted();
        }
        if (channel != null && !channel.isShutdown()) {
            try {
                channel.shutdownNow();
            } catch (Throwable ignored) {
            }
        }
    }
}
