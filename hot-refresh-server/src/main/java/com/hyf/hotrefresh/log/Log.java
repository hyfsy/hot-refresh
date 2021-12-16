package com.hyf.hotrefresh.log;

import com.hyf.hotrefresh.Constants;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class Log {

    public static final String LOG_HOME = System.getProperty("user.home") + File.separator + ".hot-refresh" + File.separator + "logs";

    /**
     * 日志方法
     *
     * @param message 打印的消息
     */
    public static void debug(String message) {
        if (isDebugEnabled()) {
            log(message, 3, null);
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date()) + " ";
            appendLog(time + " [DEBUG] " + message);
        }
    }

    public static boolean isDebugEnabled() {
        return Constants.LOG_LEVEL == 1;
    }

    public static void info(String message) {
        log(message, 2, null);
    }

    public static void warn(String message) {
        log(message, 1, null);
    }

    public static void error(String message, Throwable e) {
        log(message, 0, e);
    }

    public static void log(String message, int level, Throwable ex) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());

        if (level == 3) {
            appendConsole(time + " [DEBUG] " + message);
        }
        else if (level == 2) {
            appendConsole(time + " [INFO]  " + message);
        }
        else if (level == 1) {
            appendConsole(time + " [WARN]  " + message);
        }
        else if (level == 0) {
            appendConsole(time + " [ERROR] " + message);
            ex.printStackTrace();
        }
    }

    private static void appendConsole(String message) {
        System.out.println(message);
    }

    private static void appendLog(String message) {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(getLogFile(), true))) {
            bos.write(message.getBytes(StandardCharsets.UTF_8), 0, message.getBytes(StandardCharsets.UTF_8).length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getLogFile() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        File file = new File(LOG_HOME, "hot-refresh.log." + sdf.format(new Date()) + ".log");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        return file;
    }
}
