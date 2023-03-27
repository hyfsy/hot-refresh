package com.hyf.hotrefresh.plugin.grpc.generate;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
// @javax.annotation.Generated(
//     value = "by gRPC proto compiler (version 1.39.0)",
//     comments = "Source: remoting_api.proto")
public final class RemotingApiGrpc {

  private RemotingApiGrpc() {}

  public static final String SERVICE_NAME = "hotrefresh.v1.RemotingApi";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<Message,
      Message> getRequestMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Request",
      requestType = Message.class,
      responseType = Message.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<Message,
      Message> getRequestMethod() {
    io.grpc.MethodDescriptor<Message, Message> getRequestMethod;
    if ((getRequestMethod = RemotingApiGrpc.getRequestMethod) == null) {
      synchronized (RemotingApiGrpc.class) {
        if ((getRequestMethod = RemotingApiGrpc.getRequestMethod) == null) {
          RemotingApiGrpc.getRequestMethod = getRequestMethod =
              io.grpc.MethodDescriptor.<Message, Message>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Request"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Message.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Message.getDefaultInstance()))
              .setSchemaDescriptor(new RemotingApiMethodDescriptorSupplier("Request"))
              .build();
        }
      }
    }
    return getRequestMethod;
  }

  private static volatile io.grpc.MethodDescriptor<Message,
      Message> getRequestStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestStream",
      requestType = Message.class,
      responseType = Message.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<Message,
      Message> getRequestStreamMethod() {
    io.grpc.MethodDescriptor<Message, Message> getRequestStreamMethod;
    if ((getRequestStreamMethod = RemotingApiGrpc.getRequestStreamMethod) == null) {
      synchronized (RemotingApiGrpc.class) {
        if ((getRequestStreamMethod = RemotingApiGrpc.getRequestStreamMethod) == null) {
          RemotingApiGrpc.getRequestStreamMethod = getRequestStreamMethod =
              io.grpc.MethodDescriptor.<Message, Message>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RequestStream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Message.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Message.getDefaultInstance()))
              .setSchemaDescriptor(new RemotingApiMethodDescriptorSupplier("RequestStream"))
              .build();
        }
      }
    }
    return getRequestStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<Message,
      Message> getResponseStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ResponseStream",
      requestType = Message.class,
      responseType = Message.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<Message,
      Message> getResponseStreamMethod() {
    io.grpc.MethodDescriptor<Message, Message> getResponseStreamMethod;
    if ((getResponseStreamMethod = RemotingApiGrpc.getResponseStreamMethod) == null) {
      synchronized (RemotingApiGrpc.class) {
        if ((getResponseStreamMethod = RemotingApiGrpc.getResponseStreamMethod) == null) {
          RemotingApiGrpc.getResponseStreamMethod = getResponseStreamMethod =
              io.grpc.MethodDescriptor.<Message, Message>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ResponseStream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Message.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Message.getDefaultInstance()))
              .setSchemaDescriptor(new RemotingApiMethodDescriptorSupplier("ResponseStream"))
              .build();
        }
      }
    }
    return getResponseStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<Message,
      Message> getBiStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "BiStream",
      requestType = Message.class,
      responseType = Message.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<Message,
      Message> getBiStreamMethod() {
    io.grpc.MethodDescriptor<Message, Message> getBiStreamMethod;
    if ((getBiStreamMethod = RemotingApiGrpc.getBiStreamMethod) == null) {
      synchronized (RemotingApiGrpc.class) {
        if ((getBiStreamMethod = RemotingApiGrpc.getBiStreamMethod) == null) {
          RemotingApiGrpc.getBiStreamMethod = getBiStreamMethod =
              io.grpc.MethodDescriptor.<Message, Message>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "BiStream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Message.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Message.getDefaultInstance()))
              .setSchemaDescriptor(new RemotingApiMethodDescriptorSupplier("BiStream"))
              .build();
        }
      }
    }
    return getBiStreamMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RemotingApiStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RemotingApiStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RemotingApiStub>() {
        @Override
        public RemotingApiStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RemotingApiStub(channel, callOptions);
        }
      };
    return RemotingApiStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RemotingApiBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RemotingApiBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RemotingApiBlockingStub>() {
        @Override
        public RemotingApiBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RemotingApiBlockingStub(channel, callOptions);
        }
      };
    return RemotingApiBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RemotingApiFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RemotingApiFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RemotingApiFutureStub>() {
        @Override
        public RemotingApiFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RemotingApiFutureStub(channel, callOptions);
        }
      };
    return RemotingApiFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class RemotingApiImplBase implements io.grpc.BindableService {

    /**
     */
    public void request(Message request,
                        io.grpc.stub.StreamObserver<Message> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRequestMethod(), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<Message> requestStream(
        io.grpc.stub.StreamObserver<Message> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getRequestStreamMethod(), responseObserver);
    }

    /**
     */
    public void responseStream(Message request,
                               io.grpc.stub.StreamObserver<Message> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getResponseStreamMethod(), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<Message> biStream(
        io.grpc.stub.StreamObserver<Message> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getBiStreamMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRequestMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                Message,
                Message>(
                  this, METHODID_REQUEST)))
          .addMethod(
            getRequestStreamMethod(),
            io.grpc.stub.ServerCalls.asyncClientStreamingCall(
              new MethodHandlers<
                Message,
                Message>(
                  this, METHODID_REQUEST_STREAM)))
          .addMethod(
            getResponseStreamMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                Message,
                Message>(
                  this, METHODID_RESPONSE_STREAM)))
          .addMethod(
            getBiStreamMethod(),
            io.grpc.stub.ServerCalls.asyncBidiStreamingCall(
              new MethodHandlers<
                Message,
                Message>(
                  this, METHODID_BI_STREAM)))
          .build();
    }
  }

  /**
   */
  public static final class RemotingApiStub extends io.grpc.stub.AbstractAsyncStub<RemotingApiStub> {
    private RemotingApiStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected RemotingApiStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RemotingApiStub(channel, callOptions);
    }

    /**
     */
    public void request(Message request,
                        io.grpc.stub.StreamObserver<Message> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRequestMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<Message> requestStream(
        io.grpc.stub.StreamObserver<Message> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getRequestStreamMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    public void responseStream(Message request,
                               io.grpc.stub.StreamObserver<Message> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getResponseStreamMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<Message> biStream(
        io.grpc.stub.StreamObserver<Message> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(
          getChannel().newCall(getBiStreamMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   */
  public static final class RemotingApiBlockingStub extends io.grpc.stub.AbstractBlockingStub<RemotingApiBlockingStub> {
    private RemotingApiBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected RemotingApiBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RemotingApiBlockingStub(channel, callOptions);
    }

    /**
     */
    public Message request(Message request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRequestMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<Message> responseStream(
        Message request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getResponseStreamMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class RemotingApiFutureStub extends io.grpc.stub.AbstractFutureStub<RemotingApiFutureStub> {
    private RemotingApiFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected RemotingApiFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RemotingApiFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<Message> request(
        Message request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRequestMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REQUEST = 0;
  private static final int METHODID_RESPONSE_STREAM = 1;
  private static final int METHODID_REQUEST_STREAM = 2;
  private static final int METHODID_BI_STREAM = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final RemotingApiImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(RemotingApiImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REQUEST:
          serviceImpl.request((Message) request,
              (io.grpc.stub.StreamObserver<Message>) responseObserver);
          break;
        case METHODID_RESPONSE_STREAM:
          serviceImpl.responseStream((Message) request,
              (io.grpc.stub.StreamObserver<Message>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REQUEST_STREAM:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.requestStream(
              (io.grpc.stub.StreamObserver<Message>) responseObserver);
        case METHODID_BI_STREAM:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.biStream(
              (io.grpc.stub.StreamObserver<Message>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class RemotingApiBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RemotingApiBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return RemotingApiProto.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RemotingApi");
    }
  }

  private static final class RemotingApiFileDescriptorSupplier
      extends RemotingApiBaseDescriptorSupplier {
    RemotingApiFileDescriptorSupplier() {}
  }

  private static final class RemotingApiMethodDescriptorSupplier
      extends RemotingApiBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RemotingApiMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (RemotingApiGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RemotingApiFileDescriptorSupplier())
              .addMethod(getRequestMethod())
              .addMethod(getRequestStreamMethod())
              .addMethod(getResponseStreamMethod())
              .addMethod(getBiStreamMethod())
              .build();
        }
      }
    }
    return result;
  }
}
