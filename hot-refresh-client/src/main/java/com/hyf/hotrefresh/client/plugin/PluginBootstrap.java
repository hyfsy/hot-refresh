package com.hyf.hotrefresh.client.plugin;

import com.hyf.hotrefresh.common.Log;

import java.util.ServiceLoader;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class PluginBootstrap {

    public void boot() {
        PluginClassLoader classLoader = PluginClassLoader.getInstance();
        ServiceLoader<Plugin> plugins = ServiceLoader.load(Plugin.class, classLoader);
        for (Plugin plugin : plugins) {
            try {
                plugin.setup();
            } catch (Exception e) {
                Log.error("Plugin setup failed", e);
            }
        }
    }
}
