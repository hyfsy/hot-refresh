package com.hyf.hotrefresh.client.watch;

import com.hyf.hotrefresh.client.plugin.Plugin;
import com.hyf.hotrefresh.client.core.rpc.RpcPushWatcher;

import java.util.ServiceLoader;

import static com.hyf.hotrefresh.common.Constants.WATCH_HOME;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class FileWatcherPlugin implements Plugin {

    @Override
    public void setup() throws Exception {
        WatchCenter.registerWatcher(WATCH_HOME, new RpcPushWatcher());
        initWatcher();
    }

    private static void initWatcher() {
        ServiceLoader<Watcher> watchers = ServiceLoader.load(Watcher.class);
        for (Watcher watcher : watchers) {
            WatchCenter.registerWatcher(WATCH_HOME, watcher);
        }
    }
}
