package com.hyf.hotrefresh.remoting.server.embedded;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.remoting.exception.ServerException;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author baB_hyf
 * @date 2022/08/21
 */
public class EmbeddedRpcServerBootstrap {

    private final Map<SocketOption<?>, Object> options      = new HashMap<>();
    private final Map<SocketOption<?>, Object> childOptions = new HashMap<>();


    private ExecutorService boss;
    private ExecutorService worker;
    private SocketAddress   localAddress;
    private RequestHandler  requestHandler;
    private int             backlog = 1024;

    private int             selectorParallel = 1;
    private SelectorWrapper selectorWrapper;
    private Thread          bootThread;

    public EmbeddedRpcServerBootstrap group(ExecutorService executorService) {
        this.boss = executorService;
        this.worker = executorService;
        return this;
    }

    public EmbeddedRpcServerBootstrap group(ExecutorService boss, ExecutorService worker) {
        this.boss = boss;
        this.worker = worker;
        if (boss instanceof ThreadPoolExecutor) {
            this.selectorParallel = ((ThreadPoolExecutor) boss).getCorePoolSize();
        }
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
        init();
        return localAddress;
    }

    public void shutdownGracefully() {
        if (bootThread != null) {
            bootThread.interrupt();
        }
        if (worker != null) {
            worker.shutdown();
        }
        if (boss != null) {
            boss.shutdown();
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

    private void init() throws ServerException {
        try {
            prepare();
            start();
        } catch (IOException e) {
            throw new ServerException("Failed to start server", e);
        }
    }

    private void prepare() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(localAddress, backlog);
        ssc.configureBlocking(false);
        for (SocketOption<?> op : options.keySet()) {
            ssc.setOption((SocketOption) op, options.get(op));
        }

        Selector[] selectors = new Selector[selectorParallel];
        this.selectorWrapper = new SelectorWrapper(selectors);
        for (int i = 0; i < selectorParallel; i++) {
            selectors[i] = Selector.open();
            SelectionKey key = ssc.register(selectors[i], 0);
            key.interestOps(SelectionKey.OP_ACCEPT);
            key.attach(new Acceptor(ssc, selectorWrapper, boss, worker, childOptions, requestHandler));
        }
    }

    private void start() {
        bootThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        Selector selector = selectorWrapper.chooseSelector();
                        selector.select();
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
        }, "Selector");
        bootThread.start();
    }

    private void dispatch(SelectionKey key) {
        Runnable r = (Runnable) key.attachment();
        r.run();
    }

    private static class SelectorWrapper {
        private final Selector[] selectors;
        private       int        nextIdx = 0; // TODO 是否需要考虑？

        public SelectorWrapper(Selector[] selectors) {
            this.selectors = selectors;
        }

        public Selector chooseSelector() {
            Selector selector = selectors[nextIdx++ & selectors.length];
            if (nextIdx == selectors.length) {
                nextIdx = 0;
            }
            return selector;
        }
    }

    private static class Acceptor implements Runnable {

        private final ServerSocketChannel          serverSocketChannel;
        private final SelectorWrapper              selectorWrapper;
        private final ExecutorService              boss;
        private final ExecutorService              worker;
        private final Map<SocketOption<?>, Object> childOptions;
        private final RequestHandler               requestHandler;

        public Acceptor(ServerSocketChannel serverSocketChannel, SelectorWrapper selectorWrapper, ExecutorService boss, ExecutorService worker, Map<SocketOption<?>, Object> childOptions, RequestHandler requestHandler) throws IOException {
            this.serverSocketChannel = serverSocketChannel;
            this.selectorWrapper = selectorWrapper;
            this.boss = boss;
            this.worker = worker;
            this.childOptions = childOptions;
            this.requestHandler = requestHandler;
        }

        @Override
        public void run() {
            Selector selector = selectorWrapper.chooseSelector();

            boss.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        SocketChannel sc = serverSocketChannel.accept();
                        sc.configureBlocking(false);
                        for (SocketOption<?> op : childOptions.keySet()) {
                            sc.setOption((SocketOption) op, childOptions.get(op));
                        }
                        new Handler(sc, selector, worker, requestHandler);
                    } catch (IOException e) {
                        if (Log.isDebugMode()) {
                            Log.error("Failed to accept connection", e);
                        }
                    }
                }
            });
        }
    }

    private static class Handler implements Runnable {

        public static int READING = 0, PROCESSING = 1, WRITING = 2;

        private final SocketChannel   sc;
        private final SelectionKey    key;
        private final RequestHandler  requestHandler;
        private final ExecutorService worker;

        private volatile int        state = READING;
        private volatile ByteBuffer request;
        private volatile ByteBuffer response;

        public Handler(SocketChannel sc, Selector selector, ExecutorService worker, RequestHandler requestHandler) throws IOException {
            this.sc = sc;
            this.key = sc.register(selector, SelectionKey.OP_READ, this);
            this.worker = worker;
            this.requestHandler = requestHandler;
            selector.wakeup(); // fast wakeup to handle read operation?
        }

        @Override
        public void run() {
            try {
                if (state == READING) {
                    read();
                }
                else if (state == WRITING) {
                    write();
                }
            } catch (Throwable t) {
                if (Log.isDebugMode()) {
                    Log.error("Failed to handle connection", t);
                }
            }
        }

        private void read() {
            try {
                this.request = requestHandler.read(this.sc);
                this.state = Handler.PROCESSING;
                worker.submit(new Processor(this));
            } catch (Throwable t) {
                requestHandler.caught(this.sc, null, t);
            }
        }

        private void write() {
            try {
                requestHandler.write(this.sc, this.response);
                this.sc.close();
                this.key.cancel();
            } catch (Throwable t) {
                requestHandler.caught(this.sc, null, t);
            }
        }

        public SocketChannel getSocketChannel() {
            return this.sc;
        }

        public ByteBuffer getRequest() {
            return this.request;
        }

        public void setResponse(ByteBuffer response) {
            this.response = response;
            this.state = Handler.WRITING;
            this.key.interestOps(SelectionKey.OP_WRITE);
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
            ByteBuffer response = null;
            try {
                response = handler.getRequestHandler().handle(handler.getRequest());
            } catch (Throwable t) {
                handler.getRequestHandler().caught(handler.getSocketChannel(), handler.getRequest(), t);
            } finally {
                handler.setResponse(response);
            }
        }
    }
}
