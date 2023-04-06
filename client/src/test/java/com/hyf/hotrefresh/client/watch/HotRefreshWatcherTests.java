package com.hyf.hotrefresh.client.watch;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.args.ArgumentHolder;
import com.hyf.hotrefresh.common.util.FastReflectionUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author baB_hyf
 * @date 2023/04/05
 */
public class HotRefreshWatcherTests {

    @Test(expected = IllegalArgumentException.class)
    public void testCheckWatchHome() {
        ArgumentHolder.put(Constants.ARG_WATCH_HOME, "test");
        HotRefreshWatcher watcher = HotRefreshWatcher.getInstance();
        FastReflectionUtils.fastSetField(watcher, "watchHome", "test");
        watcher.startWatch();
        watcher.stopWatch();
    }

    @Test
    public void testStartStopWatch() throws IOException {
        Path tempDirectory = Files.createTempDirectory(null);
        try {
            ArgumentHolder.put(Constants.ARG_WATCH_HOME, tempDirectory.toFile().getAbsolutePath());
            HotRefreshWatcher watcher = HotRefreshWatcher.getInstance();
            FastReflectionUtils.fastSetField(watcher, "watchHome", tempDirectory.toFile().getAbsolutePath());
            watcher.stopWatch();
            watcher.startWatch();
            watcher.stopWatch();
            watcher.startWatch();
            watcher.stopWatch();
        } finally {
            Files.delete(tempDirectory);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResetWatchHome() throws IOException {
        Path tempDirectory = Files.createTempDirectory(null);
        ArgumentHolder.put(Constants.ARG_WATCH_HOME, tempDirectory.toFile().getAbsolutePath());
        HotRefreshWatcher watcher = HotRefreshWatcher.getInstance();
        try {
            FastReflectionUtils.fastSetField(watcher, "watchHome", tempDirectory.toFile().getAbsolutePath());
            watcher.startWatch();
            watcher.resetWatchHome("xxx");
        } finally {
            watcher.stopWatch();
            Files.delete(tempDirectory);
        }
    }
}
