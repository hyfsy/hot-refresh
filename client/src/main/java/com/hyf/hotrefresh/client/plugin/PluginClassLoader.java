package com.hyf.hotrefresh.client.plugin;

import com.hyf.hotrefresh.client.LocalClient;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class PluginClassLoader extends URLClassLoader {

    private static final PluginClassLoader INSTANCE = new PluginClassLoader(LocalClient.class.getClassLoader());

    static {
        registerAsParallelCapable();
    }

    public static PluginClassLoader getInstance() {
        return INSTANCE;
    }

    private PluginClassLoader(ClassLoader parent) {
        super(getPluginUrls(), parent);
    }

    private static URL[] getPluginUrls() {
        try {
            File pluginsPath = PluginPathLocator.getPath();
            if (pluginsPath != null && pluginsPath.exists()) {
                File[] files = pluginsPath.listFiles();
                if (files != null) {
                    return Arrays.stream(files).map(File::toURI).map(uri -> {
                        try {
                            return uri.toURL();
                        } catch (MalformedURLException e) {
                            return null;
                        }
                    }).filter(Objects::nonNull).toArray(URL[]::new);
                }
            }
        } catch (Exception ignore) {
        }
        return new URL[0];
    }

    @Override
    protected void addURL(URL url) {
        super.addURL(url);
    }
}
