package com.hyf.hotrefresh.client.watch;

import com.hyf.hotrefresh.client.core.rpc.RpcPushWatcher;
import com.hyf.hotrefresh.client.plugin.Plugin;
import com.hyf.hotrefresh.common.Services;

import java.util.List;

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

    private void initWatcher() {
        List<Watcher> watchers = Services.gets(Watcher.class);
        for (Watcher watcher : watchers) {
            WatchCenter.registerWatcher(WATCH_HOME, watcher);
        }
    }
}
