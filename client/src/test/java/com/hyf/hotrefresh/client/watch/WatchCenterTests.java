package com.hyf.hotrefresh.client.watch;

import com.hyf.hotrefresh.client.api.watch.Watcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class WatchCenterTests {

    @Before
    public void before() {
        MockWatcher.started = false;
        MockWatcher.stopped = false;
        MockWatcher2.started = false;
        MockWatcher2.stopped = false;
    }

    @After
    public void after() {
        WatchCenter.purge();
    }

    @Test
    public void testStartStop() throws IOException {
        Path tempDirectory = Files.createTempDirectory(null);
        try {
            MockWatcher mockWatcher = new MockWatcher();
            WatchCenter.registerWatcher(tempDirectory.toFile().getAbsolutePath(), mockWatcher);
            assertTrue(MockWatcher.started);
            assertFalse(MockWatcher.stopped);
            WatchCenter.deregisterWatcher(tempDirectory.toFile().getAbsolutePath(), mockWatcher);
            assertTrue(MockWatcher.stopped);
        } finally {
            Files.delete(tempDirectory);
        }
    }

    @Test
    public void testDeregisterAllWatcher() throws IOException {
        Path tempDirectory = Files.createTempDirectory(null);
        try {
            WatchCenter.registerWatcher(tempDirectory.toFile().getAbsolutePath(), new MockWatcher());
            WatchCenter.registerWatcher(tempDirectory.toFile().getAbsolutePath(), new MockWatcher2());
            assertTrue(MockWatcher.started);
            assertFalse(MockWatcher.stopped);
            assertTrue(MockWatcher2.started);
            assertFalse(MockWatcher2.stopped);
            WatchCenter.deregisterAllWatcher(tempDirectory.toFile().getAbsolutePath());
            assertTrue(MockWatcher.started);
            assertTrue(MockWatcher.stopped);
            assertTrue(MockWatcher2.started);
            assertTrue(MockWatcher2.stopped);
        } finally {
            Files.delete(tempDirectory);
        }
    }

    @Test
    public void testPurge() throws IOException {
        Path tempDirectory = Files.createTempDirectory(null);
        try {
            WatchCenter.registerWatcher(tempDirectory.toFile().getAbsolutePath(), new MockWatcher());
            WatchCenter.purge();
            assertTrue(MockWatcher.stopped);
        } finally {
            Files.delete(tempDirectory);
        }
    }

    public static class MockWatcher implements Watcher {

        private static boolean started = false;
        private static boolean stopped = false;

        @Override
        public void startWatch() {
            if (started) {
                throw new IllegalStateException();
            }
            started = true;
        }

        @Override
        public void stopWatch() {
            if (stopped) {
                throw new IllegalStateException();
            }
            stopped = true;
        }
    }

    public static class MockWatcher2 implements Watcher {

        private static boolean started = false;
        private static boolean stopped = false;

        @Override
        public void startWatch() {
            if (started) {
                throw new IllegalStateException();
            }
            started = true;
        }

        @Override
        public void stopWatch() {
            if (stopped) {
                throw new IllegalStateException();
            }
            stopped = true;
        }
    }

}
