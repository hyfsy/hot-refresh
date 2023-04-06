package com.hyf.hotrefresh.client.plugin;

import com.hyf.hotrefresh.client.api.plugin.Plugin;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author baB_hyf
 * @date 2023/04/05
 */
public class PluginManagerTests {

    @Test
    public void testInstallUnInstall() {
        PluginManager pluginManager = PluginManager.getInstance();
        assertTrue(MockPlugin.initialized);
        assertFalse(MockPlugin.started);
        pluginManager.uninstall();
        assertFalse(MockPlugin.started);
        pluginManager.install();
        assertTrue(MockPlugin.started);
        pluginManager.install();
        assertTrue(MockPlugin.started);
        pluginManager.uninstall();
        assertFalse(MockPlugin.started);
        pluginManager.uninstall();
        assertFalse(MockPlugin.started);
        pluginManager.install();
        assertTrue(MockPlugin.started);
    }

    public static class MockPlugin implements Plugin {

        private static boolean initialized;
        private static boolean started = false;

        public MockPlugin() {
            initialized = true;
        }

        @Override
        public void install() throws Exception {
            if (started) {
                throw new IllegalStateException();
            }
            started = true;
        }

        @Override
        public void uninstall() {
            if (!started) {
                throw new IllegalStateException();
            }
            started = false;
        }
    }
}
