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

import static com.hyf.hotrefresh.remoting.server.embedded.EmbeddedServerConfig.TCP_DEBUG;

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
            key.attach(new Acceptor(ssc, key, boss, worker, childOptions, requestHandler));
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
        }, "EmbeddedServerSelector");
        bootThread.start();
    }

    private void dispatch(SelectionKey key) {
        Runnable r = (Runnable) key.attachment();
        r.run();
    }

    private static class SelectorWrapper {
        private final Selector[] selectors;
        private       int        nextIdx = 0; // 单线程内被使用，无需考虑并发问题

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
        private final SelectionKey                 key;
        private final Selector                     selector;
        private final ExecutorService              boss;
        private final ExecutorService              worker;
        private final Map<SocketOption<?>, Object> childOptions;
        private final RequestHandler               requestHandler;

        public Acceptor(ServerSocketChannel serverSocketChannel, SelectionKey key, ExecutorService boss, ExecutorService worker, Map<SocketOption<?>, Object> childOptions, RequestHandler requestHandler) throws IOException {
            this.serverSocketChannel = serverSocketChannel;
            this.key = key;
            this.selector = key.selector();
            this.boss = boss;
            this.worker = worker;
            this.childOptions = childOptions;
            this.requestHandler = requestHandler;
        }

        @Override
        public void run() {
            // boss.submit(new Runnable() { // 不要异步，不然register会阻塞
            //     @Override
            //     public void run() {
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
            // }
            // });
        }
    }

    private static class Handler implements Runnable {

        public static int READING = 0, PROCESSING = 1, WRITING = 2;

        private final SocketChannel        sc;
        private final SocketChannelContext scc;
        private final SelectionKey         key;
        private final RequestHandler       requestHandler;
        private final ExecutorService      worker;

        private volatile int        state          = READING;
        private volatile ByteBuffer request;
        private volatile ByteBuffer response;
        private volatile boolean    handleComplete = false;

        public Handler(SocketChannel sc, Selector selector, ExecutorService worker, RequestHandler requestHandler) throws IOException {
            this.sc = sc;
            this.scc = new SocketChannelContext(sc);
            this.key = sc.register(selector, SelectionKey.OP_READ, this);
            this.worker = worker;
            this.requestHandler = requestHandler;
            selector.wakeup(); // register selectionKey work on the next select invoke
        }

        @Override
        public void run() {
            worker.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (state == READING) {
                            if (TCP_DEBUG) {
                                Log.debug("r");
                            }
                            read();
                        }
                        else if (state == WRITING) {
                            if (TCP_DEBUG) {
                                Log.debug("w");
                            }
                            write();
                        }
                    } catch (Throwable t) {
                        caught(t);
                    }
                }
            });
        }

        private void read() throws IOException {
            this.scc.getReadLock().lock();
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
                    worker.submit(new Processor(this));
                }
            } finally {
                this.scc.getReadLock().unlock();
            }
        }

        private void write() throws IOException {
            this.scc.getWriteLock().lock();
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
                this.scc.getWriteLock().unlock();
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
            this.key.selector().wakeup();
        }

        private void close() {
            try {
                this.sc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.key.cancel();
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
                handler.getRequestHandler().caught(scc, handler.getRequest(), handler.getResponse(), t);
            } finally {
                handler.setResponse(response);
                handler.setHandleComplete();
            }
        }
    }
}
