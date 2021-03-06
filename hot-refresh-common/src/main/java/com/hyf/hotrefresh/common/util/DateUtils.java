package com.hyf.hotrefresh.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public abstract class DateUtils {

    public static String now() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public static String today() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }
}
