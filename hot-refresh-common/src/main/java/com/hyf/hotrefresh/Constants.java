package com.hyf.hotrefresh;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class Constants {

    public static final String REFRESH_API = "/hot-refresh";

    public static final String WATCH_HOME = System.getProperty("home", System.getProperty("user.dir"));

    public static final String APPLICATION_URL = System.getProperty("url", "http://localhost:8080");

    public static final String PUSH_SERVER_URL = APPLICATION_URL.endsWith("/") ? APPLICATION_URL.substring(0, APPLICATION_URL.length() - 1) + REFRESH_API : APPLICATION_URL + REFRESH_API;

    public static final Integer LOG_DEBUG = Integer.getInteger("debug", 0);

    // ================================================================================

    public static final String REFRESH_HOME = System.getProperty("user.home") + File.separator + ".hot-refresh";

    public static final String FILE_NAME_SEPARATOR = "@@@";

    public static final String MESSAGE_SEPARATOR = "\r\n";

    public static final Charset MESSAGE_ENCODING = StandardCharsets.UTF_8;
}
