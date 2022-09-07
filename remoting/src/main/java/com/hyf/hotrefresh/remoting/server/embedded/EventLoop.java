package com.hyf.hotrefresh.remoting.server.embedded;

import com.hyf.hotrefresh.common.Log;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author baB_hyf
 * @date 2022/09/06
 */
public class EventLoop implements Runnable, Executor {

    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean stopped = new AtomicBoolean(false);

    private final Selector selector;
    private final Executor executor;

    private volatile Thread self;

    public EventLoop(Executor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("executor is null");
        }
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException("Selector canceled", e);
        }
        this.executor = executor;
    }

    public void start() {
        if (!stopped.get() && started.compareAndSet(false, true)) {
            this.executor.execute(() -> {
                EventLoop.this.self = Thread.currentThread();
                EventLoop.this.run();
            });
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                processTasks();

                int select = selector.select();
                if (select <= 0) {
                    continue;
                }

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                while (it.hasNext()) {
                    dispatch(it.next());
                    it.remove();
                }
            } catch (Throwable t) {
                if (Log.isDebugMode()) {
                    Log.error("Failed to select key", t);
                }
            }
        }
    }

    public void addTask(Runnable r) {
        taskQueue.add(r);
        selector.wakeup();
    }

    public boolean inEventLoop() {
        return Thread.currentThread() == self;
    }

    @Override
    public void execute(Runnable command) {
        this.executor.execute(command);
    }

    public Future<SelectionKey> register(SelectableChannel channel, int ops, Object attachment) {
        FutureTask<SelectionKey> task = new FutureTask<>(() -> {
            SelectionKey key = channel.register(selector, 0);
            key.interestOps(ops);
            key.attach(attachment);
            return key;
        });
        addTask(task);
        return task;
    }

    public void shutdownGracefully() {
        if (stopped.compareAndSet(false, true)) {
            if (executor instanceof ExecutorService) {
                ((ExecutorService) executor).shutdown();
            }
        }
    }

    public Selector getSelector() {
        return selector;
    }

    public Executor getExecutor() {
        return executor;
    }

    private void processTasks() {
        while (!taskQueue.isEmpty()) {
            Runnable r = taskQueue.poll();
            r.run();
        }
    }

    private void dispatch(SelectionKey key) {
        Runnable r = (Runnable) key.attachment();
        r.run();
    }
}
