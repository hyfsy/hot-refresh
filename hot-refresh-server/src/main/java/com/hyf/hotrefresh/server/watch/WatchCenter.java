package com.hyf.hotrefresh.server.watch;

import com.hyf.hotrefresh.common.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.sun.nio.file.ExtendedWatchEventModifier.FILE_TREE;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class WatchCenter {

    private static final Map<String, WatchTask> WATCH_TASK_MAP = new HashMap<>();

    private static final FileSystem FILESYSTEM = FileSystems.getDefault();

    private static final AtomicBoolean PURGING = new AtomicBoolean(false);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(WatchCenter::purge));
    }

    public static synchronized void registerWatcher(String path, Watcher watcher) {
        WatchTask watchTask = WATCH_TASK_MAP.computeIfAbsent(path, p -> {
            WatchTask task = new WatchTask(p);
            task.start();
            return task;
        });
        watchTask.addWatcher(watcher);
    }

    public static synchronized void deregisterAllWatcher(String path) {
        WatchTask watchTask = WATCH_TASK_MAP.remove(path);
        if (watchTask != null) {
            watchTask.shutdown();
        }
    }

    public static synchronized void deregisterWatcher(String path, Watcher watcher) {
        WatchTask watchTask = WATCH_TASK_MAP.get(path);
        if (watchTask != null) {
            watchTask.removeWatcher(watcher);
        }
    }

    public static void purge() {
        Log.info("exiting...");

        if (!PURGING.compareAndSet(false, true)) {
            return;
        }

        WATCH_TASK_MAP.forEach((p, t) -> t.shutdown());
        WATCH_TASK_MAP.clear();

        PURGING.set(false);
    }

    static class WatchTask extends Thread {

        private static final Object FILL = new Object();

        private final Map<Watcher, Object> watchers = new ConcurrentHashMap<>();

        private final String path;

        private final WatchService watchService;

        private final ExecutorService singleExecutor;

        private volatile boolean stopped = false;

        public WatchTask(String path) {
            this.path = path;
            setName(path);
            Path p = Paths.get(path);
            if (!p.toFile().isDirectory()) {
                throw new IllegalArgumentException("Not a directory: " + p.toString());
            }

            try {
                watchService = FILESYSTEM.newWatchService();
                p.register(watchService, new WatchEvent.Kind<?>[]{ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE},
                        FILE_TREE);
            } catch (IOException e) {
                throw new IllegalStateException("WatchService start failed: " + p.toString(), e);
            }

            singleExecutor = Executors.newSingleThreadExecutor();
        }

        @Override
        public void run() {
            while (!stopped) {

                try {
                    WatchKey watchKey = watchService.take();
                    List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                    watchKey.reset();

                    if (watchEvents.isEmpty()) {
                        continue;
                    }

                    if (singleExecutor.isShutdown()) {
                        return;
                    }

                    singleExecutor.submit(() -> {
                        for (WatchEvent<?> event : watchEvents) {

                            WatchEvent.Kind<?> kind = event.kind();
                            Object context = event.context();

                            // if (OVERFLOW.equals(kind)) {
                            //     System.out.println("File watch overflow: " + context);
                            //     continue;
                            // }

                            File changedFile = new File(path, context.toString());

                            // 放过删除的场景
                            if (changedFile.isDirectory()) {
                                continue;
                            }

                            for (Watcher watcher : watchers.keySet()) {

                                if (!watcher.interest(context)) {
                                    continue;
                                }

                                if (ENTRY_CREATE.equals(kind)) {
                                    watcher.onCreate(changedFile);
                                }
                                else if (ENTRY_MODIFY.equals(kind)) {
                                    watcher.onModify(changedFile);
                                }
                                else if (ENTRY_DELETE.equals(kind)) {
                                    watcher.onDelete(changedFile);
                                }
                            }
                        }
                    });
                } catch (InterruptedException ignored) {
                    Thread.interrupted();
                } catch (Exception e) {
                    Log.error("Watch failed", e);
                }
            }
        }

        public void shutdown() {
            stopped = true;
            singleExecutor.shutdown();
        }

        public void addWatcher(Watcher watcher) {
            watchers.put(watcher, FILL);
        }

        public void removeWatcher(Watcher watcher) {
            watchers.remove(watcher);
        }

        public String getWatchPath() {
            return path;
        }
    }
}
