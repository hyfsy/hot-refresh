package com.hyf.hotrefresh.client.plugin;

import com.hyf.hotrefresh.client.api.plugin.Plugin;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.Services;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class PluginManager {

    private static final PluginManager INSTANCE = new PluginManager();

    private final AtomicBoolean installed   = new AtomicBoolean(false);
    private final AtomicBoolean uninstalled = new AtomicBoolean(true);

    private final List<Plugin> plugins = Services.gets(Plugin.class);

    private PluginManager() {
    }

    public static PluginManager getInstance() {
        return INSTANCE;
    }

    public void install() {
        if (!installed.compareAndSet(false, true)) {
            return;
        }

        for (Plugin plugin : plugins) {
            try {
                plugin.install();
            } catch (Exception e) {
                Log.error("Plugin setup failed: " + plugin.getClass().getName(), e);
            }
        }
        uninstalled.set(false);
    }

    public void uninstall() {
        if (!uninstalled.compareAndSet(false, true)) {
            return;
        }

        for (Plugin plugin : plugins) {
            try {
                plugin.uninstall();
            } catch (Exception e) {
                Log.error("Plugin uninstall failed: " + plugin.getClass().getName(), e);
            }
        }
        installed.set(false);
    }
}
