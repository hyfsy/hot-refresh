package com.hyf.hotrefresh.client.watch;

import com.hyf.hotrefresh.client.core.rpc.RpcPushWatcher;
import com.hyf.hotrefresh.common.args.ArgumentHolder;

import java.io.File;

import static com.hyf.hotrefresh.common.Constants.ARG_WATCH_HOME;

/**
 * @author baB_hyf
 * @date 2023/04/05
 */
public class HotRefreshWatcher {

    private static volatile HotRefreshWatcher INSTANCE;
    private volatile        RpcPushWatcher    rpcPushWatcher;
    private volatile        String            watchHome;

    private HotRefreshWatcher(String watchHome) {
        checkWatchHome(watchHome);
        setWatchHome(watchHome);
    }

    public static HotRefreshWatcher getInstance() {
        if (INSTANCE == null) {
            synchronized (HotRefreshWatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HotRefreshWatcher(ArgumentHolder.get(ARG_WATCH_HOME));
                }
            }
        }
        return INSTANCE;
    }

    public void startWatch() {
        if (rpcPushWatcher == null) {
            rpcPushWatcher = new RpcPushWatcher();
        }
        WatchCenter.registerWatcher(watchHome, rpcPushWatcher);
    }

    public void stopWatch() {
        WatchCenter.deregisterWatcher(watchHome, rpcPushWatcher);
        rpcPushWatcher = null;
    }

    public void resetWatchHome(String newWatchHome) {
        checkWatchHome(newWatchHome);
        boolean needRestart = rpcPushWatcher != null;
        if (needRestart) {
            stopWatch();
        }
        setWatchHome(newWatchHome);
        if (needRestart) {
            startWatch();
        }
    }

    private void checkWatchHome(String watchHome) {
        File file;
        try {
            file = new File(watchHome);
        } catch (Exception e) {
            throw new IllegalArgumentException("Watch home path invalid: " + watchHome, e);
        }

        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Watch home path is not directory");
        }
    }

    private void setWatchHome(String watchHome) {
        this.watchHome = watchHome;
    }
}
