package com.hyf.hotrefresh.common;

import com.hyf.hotrefresh.common.util.DateUtils;
import com.hyf.hotrefresh.common.util.ExceptionUtils;
import com.hyf.hotrefresh.common.util.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * TODO 服务端和客户端在同一机器上会导致打印两次的问题
 *
 * @author baB_hyf
 * @date 2021/12/12
 */
public class Log {

    public static final String LOG_HOME = Constants.REFRESH_HOME + File.separator + "logs";

    public static boolean isDebugMode() {
        return Constants.DEBUG;
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
        debug(ExceptionUtils.getStackMessage(e));
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

        String now = DateUtils.now();

        if (level == 3) {
            return now + " [DEBUG] " + message;
        }
        else if (level == 2) {
            return now + " [INFO]  " + message;
        }
        else if (level == 1) {
            return now + " [WARN]  " + message;
        }
        else if (level == 0) {
            return now + " [ERROR] " + message;
        }

        throw new UnsupportedOperationException("Log level not support: " + level);
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
        String fileName = "hot-refresh.log." + DateUtils.today() + ".log";
        return FileUtils.getFile(LOG_HOME + File.separator + fileName);
    }
}
