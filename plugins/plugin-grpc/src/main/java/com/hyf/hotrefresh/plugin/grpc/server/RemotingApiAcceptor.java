package com.hyf.hotrefresh.plugin.grpc.server;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.plugin.grpc.generate.Message;
import com.hyf.hotrefresh.plugin.grpc.generate.RemotingApiGrpc;
import com.hyf.hotrefresh.plugin.grpc.utils.MessageUtils;
import io.grpc.stub.StreamObserver;

/**
 * @author baB_hyf
 * @date 2022/10/01
 */
public class RemotingApiAcceptor extends RemotingApiGrpc.RemotingApiImplBase {

    private final GrpcServer grpcServer;

    public RemotingApiAcceptor(GrpcServer grpcServer) {
        this.grpcServer = grpcServer;
    }

    @Override
    public void request(Message request, StreamObserver<Message> responseObserver) {
        this.responseStream(request, responseObserver);
    }

    @Override
    public StreamObserver<Message> requestStream(StreamObserver<Message> responseObserver) {
        return this.biStream(responseObserver);
    }

    @Override
    public void responseStream(Message request, StreamObserver<Message> responseObserver) {
        com.hyf.hotrefresh.remoting.message.Message response = grpcServer.handle(MessageUtils.convert(request));
        responseObserver.onNext(MessageUtils.convert(response));
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Message> biStream(StreamObserver<Message> responseObserver) {
        return new StreamObserver<Message>() {
            @Override
            public void onNext(Message message) {
                RemotingApiAcceptor.this.responseStream(message, responseObserver);
            }

            @Override
            public void onError(Throwable t) {
                Log.error("Failed to send request", t);
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
