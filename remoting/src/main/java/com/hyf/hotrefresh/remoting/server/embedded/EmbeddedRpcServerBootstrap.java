package com.hyf.hotrefresh.remoting.server.embedded;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.remoting.exception.ServerException;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

import static com.hyf.hotrefresh.remoting.server.embedded.EmbeddedServerConfig.TCP_DEBUG;

/**
 * @author baB_hyf
 * @date 2022/08/21
 */
public class EmbeddedRpcServerBootstrap {

    private final Map<SocketOption<?>, Object> options      = new HashMap<>();
    private final Map<SocketOption<?>, Object> childOptions = new HashMap<>();

    private EventLoop      boss;
    private EventLoop      worker;
    private SocketAddress  localAddress;
    private RequestHandler requestHandler;
    private int            backlog = 1024;

    public EmbeddedRpcServerBootstrap group(EventLoop eventLoop) {
        this.boss = eventLoop;
        this.worker = eventLoop;
        return this;
    }

    public EmbeddedRpcServerBootstrap group(EventLoop boss, EventLoop worker) {
        this.boss = boss;
        this.worker = worker;
        return this;
    }

    public EmbeddedRpcServerBootstrap option(SocketOption<?> option, Object value) {
        options.put(option, value);
        return this;
    }

    public EmbeddedRpcServerBootstrap childOption(SocketOption<?> option, Object value) {
        childOptions.put(option, value);
        return this;
    }

    public EmbeddedRpcServerBootstrap localAddress(SocketAddress localAddress) {
        this.localAddress = localAddress;
        return this;
    }

    public EmbeddedRpcServerBootstrap childHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
        return this;
    }

    // specific

    public EmbeddedRpcServerBootstrap backlog(int backlog) {
        this.backlog = backlog;
        return this;
    }

    public SocketAddress bind() throws ServerException {
        check();
        doBind();
        return localAddress;
    }

    public void shutdownGracefully() {
        if (boss != null) {
            boss.shutdownGracefully();
        }
        if (worker != null) {
            worker.shutdownGracefully();
        }
    }

    private void check() {
        if (boss == null || worker == null) {
            throw new IllegalArgumentException("boss and worker must not be null");
        }
        if (localAddress == null) {
            throw new IllegalArgumentException("localAddress must not be null");
        }
        if (requestHandler == null) {
            throw new IllegalArgumentException("requestHandler must not be null");
        }
        if (backlog <= 0) {
            throw new IllegalArgumentException("backlog must greater than 0");
        }
    }

    private void doBind() throws ServerException {
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.bind(localAddress, backlog);
            ssc.configureBlocking(false);
            for (SocketOption<?> op : options.keySet()) {
                ssc.setOption((SocketOption) op, options.get(op));
            }

            boss.start();
            new Acceptor(ssc, boss, worker, childOptions, requestHandler);
        } catch (IOException e) {
            throw new ServerException("Failed to start server", e);
        }
    }

    private static class Acceptor implements Runnable {

        private final SelectorWrapper              selectorWrapper;
        private final ServerSocketChannel          serverSocketChannel;
        private final EventLoop                    boss;
        private final Map<SocketOption<?>, Object> childOptions;
        private final RequestHandler               requestHandler;

        public Acceptor(ServerSocketChannel serverSocketChannel, EventLoop boss, EventLoop worker, Map<SocketOption<?>, Object> childOptions, RequestHandler requestHandler) throws IOException {
            this.selectorWrapper = new SelectorWrapper(worker.getExecutor());
            this.serverSocketChannel = serverSocketChannel;
            this.boss = boss;
            this.childOptions = childOptions;
            this.requestHandler = requestHandler;
            boss.register(serverSocketChannel, SelectionKey.OP_ACCEPT, this);
        }

        @Override
        public void run() {
            if (boss.inEventLoop()) {
                accept();
            }
            else {
                boss.addTask(this::accept);
            }
        }

        private void accept() {
            try {
                SocketChannel sc = serverSocketChannel.accept();
                if (sc != null) {
                    sc.configureBlocking(false);
                    for (SocketOption<?> op : childOptions.keySet()) {
                        sc.setOption((SocketOption) op, childOptions.get(op));
                    }
                    new Handler(sc, selectorWrapper.chooseSelector(), requestHandler);
                }
            } catch (IOException e) {
                if (Log.isDebugMode()) {
                    Log.error("Failed to accept connection", e);
                }
            }
        }
    }

    private static class SelectorWrapper {
        private static final int         selectorParallel = 1;
        private final        AtomicLong  nextIdx          = new AtomicLong();
        private final        EventLoop[] eventLoops;

        public SelectorWrapper(Executor executor) {
            EventLoop[] eventLoops = new EventLoop[selectorParallel];
            for (int i = 0; i < selectorParallel; i++) {
                EventLoop eventLoop = new EventLoop(executor);
                eventLoop.start();
                eventLoops[i] = eventLoop;
            }
            this.eventLoops = eventLoops;
        }

        public EventLoop chooseSelector() {
            return eventLoops[(int) Math.abs(nextIdx.getAndIncrement() % eventLoops.length)];
        }
    }

    private static class Handler implements Runnable {

        public static int READING = 0, PROCESSING = 1, WRITING = 2;

        private final SocketChannelContext scc;
        private final EventLoop            worker;
        private final RequestHandler       requestHandler;

        private SelectionKey key;

        private volatile int        state          = READING;
        private volatile ByteBuffer request;
        private volatile ByteBuffer response;
        private volatile boolean    handleComplete = false;

        public Handler(SocketChannel sc, EventLoop worker, RequestHandler requestHandler) throws IOException {
            this.scc = new SocketChannelContext(sc);
            this.worker = worker;
            this.requestHandler = requestHandler;
            this.doRegister(sc, worker);
        }

        private void doRegister(SocketChannel sc, EventLoop eventLoop) {
            Future<SelectionKey> future = eventLoop.register(sc, SelectionKey.OP_READ, Handler.this);
            try {
                this.key = future.get();
            } catch (Throwable e) {
                close();
                if (Log.isDebugMode()) {
                    Log.error("Failed to register read io event", e);
                }
            }
        }

        @Override
        public void run() {
            if (worker.inEventLoop()) {
                process();
            }
            else {
                worker.addTask(this::process);
            }
        }

        private void process() {
            try {
                if (state == READING) {
                    if (TCP_DEBUG) {
                        Log.info("r");
                    }
                    read();
                }
                else if (state == WRITING) {
                    if (TCP_DEBUG) {
                        Log.info("w");
                    }
                    write();
                }
            } catch (Throwable t) {
                caught(t);
            }
        }

        private void read() throws IOException {
            Lock readLock = this.scc.getReadLock();
            readLock.lock();
            try {
                if (this.scc.isReadComplete()) {
                    return;
                }
                this.request = requestHandler.read(this.scc);
                if (this.request != null) {
                    this.scc.setReadComplete(true);
                }
                if (this.scc.isReadComplete()) {
                    this.state = Handler.PROCESSING;
                    worker.execute(new Processor(this)); // worker threads
                }
            } finally {
                readLock.unlock();
            }
        }

        private void write() throws IOException {
            Lock writeLock = this.scc.getWriteLock();
            writeLock.lock();
            try {
                if (this.scc.isWriteComplete()) {
                    return;
                }
                requestHandler.write(this.scc, this.response);
                if (this.response != null && !this.response.hasRemaining()) {
                    this.scc.setWriteComplete(true);
                }
                if (this.scc.isWriteComplete()) {
                    reset();
                }
            } finally {
                writeLock.unlock();
            }
        }

        private void caught(Throwable t) {
            requestHandler.caught(this.scc, this.request, this.response, t);
            close();
            if (Log.isDebugMode()) {
                Log.error("Failed to handle connection", t);
            }
        }

        private void reset() {
            this.state = READING;
            this.request = null;
            this.response = null;
            this.handleComplete = false;
            this.scc.reset();
            this.key.interestOps(SelectionKey.OP_READ); // TODO 长连接管理
            this.key.selector().wakeup(); // register selectionKey work on the next select invoke
        }

        private void close() {
            if (this.key != null) {
                this.key.cancel();
            }
            this.scc.close();
        }

        public ByteBuffer getRequest() {
            return this.request;
        }

        public ByteBuffer getResponse() {
            return this.response;
        }

        public void setResponse(ByteBuffer response) {
            if (this.handleComplete) {
                return;
            }

            this.response = response;
            this.state = Handler.WRITING;
            this.key.interestOps(SelectionKey.OP_WRITE);
            this.key.selector().wakeup(); // register selectionKey work on the next select invoke
        }

        public void setHandleComplete() {
            this.handleComplete = true;
        }

        public SocketChannelContext getSocketChannelContext() {
            return this.scc;
        }

        public RequestHandler getRequestHandler() {
            return requestHandler;
        }
    }

    private static class Processor implements Runnable {

        private final Handler handler;

        public Processor(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            SocketChannelContext scc = handler.getSocketChannelContext();
            ByteBuffer response = null;
            try {
                response = handler.getRequestHandler().handle(scc, handler.getRequest());
            } catch (Throwable t) {
                handler.getRequestHandler().caught(scc, handler.getRequest(), null, t);
            } finally {
                handler.setResponse(response);
                handler.setHandleComplete();
            }
        }
    }
}
