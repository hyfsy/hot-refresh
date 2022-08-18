package com.hyf.hotrefresh.common.hook;

import com.hyf.hotrefresh.common.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author baB_hyf
 * @date 2022/08/16
 */
public class ShutdownHook extends Thread {

    private static final ShutdownHook SHUTDOWN_HOOK = new ShutdownHook("HotRefreshShutdownHook");

    static {
        Runtime.getRuntime().addShutdownHook(SHUTDOWN_HOOK);
    }

    private final Set<Disposable> disposables = new HashSet<>();
    private final AtomicBoolean   destroyed   = new AtomicBoolean(false);

    private ShutdownHook(String name) {
        super(name);
    }

    public static ShutdownHook getInstance() {
        return SHUTDOWN_HOOK;
    }

    public static void removeInstance() {
        Runtime.getRuntime().removeShutdownHook(SHUTDOWN_HOOK);
    }

    public void addDisposable(Disposable disposable) {
        disposables.add(disposable);
    }

    @Override
    public void run() {
        destroyAll();
    }

    public void destroyAll() {
        if (!destroyed.compareAndSet(false, true)) {
            return;
        }

        if (disposables.isEmpty()) {
            return;
        }

        for (Disposable disposable : disposables) {
            try {
                disposable.destroy();
            } catch (Exception e) {
                Log.error("Failed to run destroy", e);
            }
        }
    }
}

