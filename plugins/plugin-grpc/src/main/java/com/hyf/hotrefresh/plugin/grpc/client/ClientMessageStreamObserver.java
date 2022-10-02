package com.hyf.hotrefresh.plugin.grpc.client;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.plugin.grpc.generate.Message;
import com.hyf.hotrefresh.plugin.grpc.utils.MessageUtils;
import com.hyf.hotrefresh.remoting.exception.ClientException;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandler;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandlerFactory;
import io.grpc.stub.StreamObserver;

/**
 * @author baB_hyf
 * @date 2022/10/01
 */
public class ClientMessageStreamObserver implements StreamObserver<Message> {

    private final MessageHandler clientMessageHandler;

    public ClientMessageStreamObserver() {
        this.clientMessageHandler = MessageHandlerFactory.getClientMessageHandler();
    }

    @Override
    public void onNext(Message message) {
        try {
            this.clientMessageHandler.handle(MessageUtils.convert(message));
        } catch (Exception e) {
            throw new ClientException("Failed to handle response", e);
        }
    }

    @Override
    public void onError(Throwable t) {
        Log.error("Grpc request failed", t);
    }

    @Override
    public void onCompleted() {
        if (Log.isDebugMode()) {
            Log.debug("Grpc request complete");
        }
    }
}
