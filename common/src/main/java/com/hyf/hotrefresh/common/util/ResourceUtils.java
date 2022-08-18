package com.hyf.hotrefresh.common.util;

import com.hyf.hotrefresh.common.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.*;

/**
 * @author baB_hyf
 * @date 2022/06/25
 */
public abstract class ResourceUtils {

    public static List<String> readPropertiesAsRows(String resourceName, ClassLoader classLoader) {
        List<String> rows = new ArrayList<>();
        try {
            Enumeration<URL> resources = classLoader.getResources(resourceName);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try (BufferedReader reader = new LineNumberReader(new InputStreamReader(url.openStream()))) {
                    String row;
                    while ((row = reader.readLine()) != null) {
                        if (!"".equals(row.trim())) {
                            rows.add(row.trim());
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log.error("Get compile options file failed", e);
        }
        return rows;
    }

    public static Map<String, String> readPropertiesAsMap(String resourceName, ClassLoader classLoader) {
        Map<String, String> propertiesMap = new LinkedHashMap<>();
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
