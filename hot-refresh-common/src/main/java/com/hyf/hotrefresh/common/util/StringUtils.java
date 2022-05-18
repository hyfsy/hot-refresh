package com.hyf.hotrefresh.common.util;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public abstract class StringUtils {

    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim());
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
