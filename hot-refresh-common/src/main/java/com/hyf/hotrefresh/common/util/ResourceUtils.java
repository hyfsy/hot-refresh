package com.hyf.hotrefresh.common.util;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author baB_hyf
 * @date 2022/06/25
 */
public abstract class ResourceUtils {

    public static List<String> readPropertiesAsList(String resourceName, ClassLoader classLoader) {
        List<String> rows = new ArrayList<>();
        Map<String, String> propertiesMap = readPropertiesAsMap(resourceName, classLoader);
        propertiesMap.forEach((k, v) -> rows.add(k + "=" + v));
        return rows;
    }

    public static Map<String, String> readPropertiesAsMap(String resourceName, ClassLoader classLoader) {
        Map<String, String> propertiesMap = new HashMap<>();
        List<URL> urls = getResource(resourceName, classLoader);
        Properties properties = new Properties();
        urls.forEach(url -> {
            try {
                properties.load(url.openStream());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read resource file", e);
            }
        });
        properties.forEach((k, v) -> propertiesMap.put(k.toString(), v.toString()));

        return propertiesMap;
    }

    public static List<URL> getResource(String resourceName, ClassLoader classLoader) {
        try {
            List<URL> urls = new ArrayList<>();
            Enumeration<URL> resources = classLoader.getResources(resourceName);
            while (resources.hasMoreElements()) {
                urls.add(resources.nextElement());
            }
            return urls;
        } catch (IOException e) {
            throw new RuntimeException("Failed to get resource file", e);
        }
    }
}
