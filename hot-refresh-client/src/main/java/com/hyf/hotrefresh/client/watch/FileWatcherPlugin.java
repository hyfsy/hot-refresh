package com.hyf.hotrefresh.client.watch;

import com.hyf.hotrefresh.client.api.watch.Watcher;
import com.hyf.hotrefresh.client.core.rpc.RpcPushWatcher;
import com.hyf.hotrefresh.client.api.plugin.Plugin;
import com.hyf.hotrefresh.common.Services;
import com.hyf.hotrefresh.common.args.ArgumentHolder;

import java.util.List;

import static com.hyf.hotrefresh.common.Constants.ARG_WATCH_HOME;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class FileWatcherPlugin implements Plugin {

    @Override
    public void setup() throws Exception {
        WatchCenter.registerWatcher(ArgumentHolder.get(ARG_WATCH_HOME), new RpcPushWatcher());
        initWatcher();
    }

    private void initWatcher() {
        List<Watcher> watchers = Services.gets(Watcher.class);
        for (Watcher watcher : watchers) {
            WatchCenter.registerWatcher(ArgumentHolder.get(ARG_WATCH_HOME), watcher);
        }
    }
}
