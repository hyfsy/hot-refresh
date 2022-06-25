package com.hyf.hotrefresh.client.plugin;

import com.hyf.hotrefresh.common.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class PluginPathLocator {

    public static final String PLUGIN_DIR_NAME = "plugins";

    private static volatile File PLUGIN_PATH;

    public static File getPath() {
        if (PLUGIN_PATH == null) {
            File file = findPath();
            PLUGIN_PATH = new File(file, PLUGIN_DIR_NAME);
            if (!PLUGIN_PATH.exists()) {
                Log.warn("plugin path not exists: " + PLUGIN_PATH.getAbsolutePath());
            }
        }
        return PLUGIN_PATH;
    }

    private static File findPath() {
        String classResourcePath = PluginPathLocator.class.getName().replaceAll("\\.", "/") + ".class";

        URL resource = ClassLoader.getSystemClassLoader().getResource(classResourcePath);
        if (resource != null) {
            String urlString = resource.toString();

            int insidePathIndex = urlString.indexOf('!');
            boolean isInJar = insidePathIndex > -1;

            if (isInJar) {
                urlString = urlString.substring(urlString.indexOf("file:"), insidePathIndex);
                File jarFile = null;
                try {
                    jarFile = new File(new URL(urlString).toURI());
                } catch (MalformedURLException | URISyntaxException e) {
                    if (Log.isDebugMode()) {
                        Log.error("File url invalid", e);
                    }
                }
                if (jarFile != null && jarFile.exists()) {
                    return jarFile.getParentFile();
                }
            }
            else {
                int prefixLength = "file:".length();
                String classLocation =
                        urlString.substring(prefixLength, urlString.length() - classResourcePath.length());
                return new File(classLocation);
            }
        }

        throw new RuntimeException("Cannot locate jar file");
    }
}
