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
        ServiceLoader<Pluggable> plugins = ServiceLoader.load(Pluggable.class, classLoader);
        for (Pluggable plugin : plugins) {
            try {
                plugin.setup();
            } catch (Exception e) {
                Log.error("Plugin setup failed", e);
            }
        }
    }
}
