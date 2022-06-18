package com.hyf.hotrefresh.common;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class Constants {

    public static final String REFRESH_HOME = System.getProperty("user.home") + File.separator + ".hot-refresh";

    public static final String REFRESH_API = "/hot-refresh";

    public static final String FILE_NAME_SEPARATOR = "@@@";

    public static final String MESSAGE_SEPARATOR = "\r\n";

    public static final Charset MESSAGE_ENCODING = StandardCharsets.UTF_8;

    // args

    public static final String ARG_DEBUG = "debug";
    public static final String ARG_WATCH_HOME = "watchHome";
    public static final String ARG_SERVER_URL = "serverURL";

}
