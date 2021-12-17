package com.hyf.hotrefresh.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class ExceptionUtil {

    public static String getNestedMessage(Throwable t) {
        StringBuilder sb = new StringBuilder();
        while (t != null) {
            if (sb.length() != 0) {
                sb.append("; nested exception is ");
            }
            sb.append(t);
            t = t.getCause();
        }
        return sb.toString();
    }

    public static String getStackMessage(Throwable t) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        t.printStackTrace(ps);
        return baos.toString();
    }
}
