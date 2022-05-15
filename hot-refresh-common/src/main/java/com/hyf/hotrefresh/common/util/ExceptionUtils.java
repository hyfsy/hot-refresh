package com.hyf.hotrefresh.common.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class ExceptionUtils {

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
        try (PrintStream ps = new PrintStream(baos)) {
            t.printStackTrace(ps);
            ps.flush();
            return baos.toString();
        }
    }
}
