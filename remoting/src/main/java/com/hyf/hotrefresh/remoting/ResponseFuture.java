package com.hyf.hotrefresh.remoting;

import com.hyf.hotrefresh.remoting.exception.RemotingExecutionException;
import com.hyf.hotrefresh.remoting.exception.RemotingInterruptedException;
import com.hyf.hotrefresh.remoting.exception.RemotingTimeoutException;
import com.hyf.hotrefresh.remoting.message.Message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author baB_hyf
 * @date 2022/08/17
 */
public class ResponseFuture {

    private final CompletableFuture<Message> future = new CompletableFuture<>();

    public Message get(long timeoutMillis) throws RemotingInterruptedException, RemotingTimeoutException, RemotingExecutionException {
        try {
            return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RemotingInterruptedException("Response result await interrupted", e);
        } catch (ExecutionException e) {
            throw new RemotingExecutionException("Response result handle failed", e);
        } catch (TimeoutException e) {
            throw new RemotingTimeoutException("Response result await timeout");
        }
    }

    public void success(Message message) {
        future.complete(message);
    }

    public void fail(Throwable t) {
        future.completeExceptionally(t);
    }

    public void setCallback(MessageCallback callback) {
        future.whenComplete(callback::handle);
    }
}
