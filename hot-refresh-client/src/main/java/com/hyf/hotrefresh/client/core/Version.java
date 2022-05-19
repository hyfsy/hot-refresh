package com.hyf.hotrefresh.client.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author baB_hyf
 * @date 2022/05/20
 */
public class Version {

    public static final String DEFAULT_VERSION = "";

    public static final String GROUP_ID              = "com.hyf.hotrefresh";
    public static final String ARTIFACT_ID           = "hot-refresh-client";
    public static final String VERSION_RESOURCE_PATH = "META-INF/maven/" + GROUP_ID + "/" + ARTIFACT_ID + "/pom.properties";

    public static String getVersion() {
        try (InputStream is = Version.class.getClassLoader().getResourceAsStream(VERSION_RESOURCE_PATH)) {
            if (is == null) {
                return DEFAULT_VERSION;
            }
            Properties properties = new Properties();
            properties.load(is);
            return properties.getProperty("version", DEFAULT_VERSION);
        } catch (IOException e) {
            return DEFAULT_VERSION;
        }
    }
}
