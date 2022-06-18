package com.hyf.hotrefresh.client.plugin;

import com.hyf.hotrefresh.client.api.plugin.Plugin;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.Services;

import java.util.List;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class PluginBootstrap {

    public void boot() {
        List<Plugin> plugins = Services.gets(Plugin.class);
        for (Plugin plugin : plugins) {
            try {
                plugin.setup();
            } catch (Exception e) {
                Log.error("Plugin setup failed", e);
            }
        }
    }
}
