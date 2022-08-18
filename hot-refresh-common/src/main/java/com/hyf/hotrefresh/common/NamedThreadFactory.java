package com.hyf.hotrefresh.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Named thread factory.
 *
 * @author baB_hyf
 * @date 2022/08/16
 */
public class NamedThreadFactory implements ThreadFactory {

    private final AtomicInteger counter = new AtomicInteger(0);
    private final ThreadGroup   group;
    private final String        prefix;
    private final boolean       makeDaemons;
    private final int           totalSize;

    /**
     * Instantiates a new Named thread factory.
     *
     * @param prefix      the prefix
     * @param totalSize   the total size
     * @param makeDaemons the make daemons
     */
    public NamedThreadFactory(String prefix, int totalSize, boolean makeDaemons) {
        SecurityManager securityManager = System.getSecurityManager();
        this.group = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.prefix = prefix;
        this.makeDaemons = makeDaemons;
        this.totalSize = totalSize;
    }

    /**
     * Instantiates a new Named thread factory.
     *
     * @param prefix      the prefix
     * @param makeDaemons the make daemons
     */
    public NamedThreadFactory(String prefix, boolean makeDaemons) {
        this(prefix, 0, makeDaemons);
    }

    /**
     * Instantiates a new Named thread factory.
     *
     * @param prefix    the prefix
     * @param totalSize the total size
     */
    public NamedThreadFactory(String prefix, int totalSize) {
        this(prefix, totalSize, true);
    }

    @Override
    public Thread newThread(Runnable r) {
        String name = prefix + "_" + counter.incrementAndGet();
        if (totalSize > 1) {
            name += "_" + totalSize;
        }
        Thread thread = createThread(group, r);
        thread.setName(name);
        thread.setDaemon(makeDaemons);
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }

    protected Thread createThread(ThreadGroup group, Runnable r) {
        return new Thread(group, r);
    }
}
