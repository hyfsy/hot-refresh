package com.hyf.hotrefresh.remoting;

import com.hyf.hotrefresh.remoting.exception.RemotingExecutionException;
import com.hyf.hotrefresh.remoting.exception.RemotingInterruptedException;
import com.hyf.hotrefresh.remoting.exception.RemotingTimeoutException;
import com.hyf.hotrefresh.remoting.message.Message;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author baB_hyf
 * @date 2022/08/17
 */
public class ResponseFuture {

    private final CountDownLatch latch = new CountDownLatch(1);

    private volatile Message   message;
    private volatile Throwable t;

    public Message get(long timeoutMillis) throws RemotingInterruptedException, RemotingTimeoutException, RemotingExecutionException {
        try {
            if (!latch.await(timeoutMillis, TimeUnit.MILLISECONDS)) {
                throw new RemotingTimeoutException("Response result await timeout");
            }

            if (t != null) {
                throw new RemotingExecutionException("Response result handle error", t);
            }

            return message;
        } catch (InterruptedException e) {
            throw new RemotingInterruptedException("Response result await interrupted", e);
        }
    }

    public void success(Message message) {
        this.message = message;
        latch.countDown();
    }

    public void fail(Throwable t) {
        this.t = t;
        latch.countDown();
    }
}
