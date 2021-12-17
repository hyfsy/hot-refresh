package com.hyf.hotrefresh;

import com.hyf.hotrefresh.util.DateUtil;
import com.hyf.hotrefresh.util.ExceptionUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class Log {

    public static final String LOG_HOME = Constants.REFRESH_HOME + File.separator + "logs";

    public static boolean isDebugMode() {
        return Constants.LOG_DEBUG == 1;
    }

    /**
     * 日志方法
     *
     * @param message 打印的消息
     */
    public static void debug(String message) {
        log(message, 3, !isDebugMode());
    }

    public static void info(String message) {
        log(message, 2, false);
    }

    public static void warn(String message) {
        log(message, 1, false);
    }

    public static void error(String message, Throwable e) {
        log(message, 0, false);
        debug(ExceptionUtil.getStackMessage(e));
    }

    private static void log(String message, int level, boolean appendFile) {
        message = formatMessage(message, level);
        if (appendFile) {
            appendFile(message);
        }
        else {
            appendConsole(message);
        }
    }

    private static String formatMessage(String message, int level) {

        String time = DateUtil.now();

        if (level == 3) {
            return time + " [DEBUG] " + message;
        }
        else if (level == 2) {
            return time + " [INFO]  " + message;
        }
        else if (level == 1) {
            return time + " [WARN]  " + message;
        }
        else if (level == 0) {
            return time + " [ERROR] " + message;
        }

        return message;
    }

    private static void appendConsole(String message) {
        System.out.println(message);
    }

    private static void appendFile(String message) {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(getLogFile(), true))) {
            bos.write(message.getBytes(Constants.MESSAGE_ENCODING), 0, message.getBytes(Constants.MESSAGE_ENCODING).length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getLogFile() {
        File file = new File(LOG_HOME, "hot-refresh.log." + DateUtil.nowDate() + ".log");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        return file;
    }
}
