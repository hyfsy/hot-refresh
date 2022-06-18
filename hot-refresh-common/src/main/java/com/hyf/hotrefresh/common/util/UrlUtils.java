package com.hyf.hotrefresh.common.util;

/**
 * @author baB_hyf
 * @date 2022/06/18
 */
public abstract class UrlUtils {

    /**
     * 移除后缀/，相对URL则添加前缀/
     *
     * @param url url路径
     * @return xxx/ -> /xxx
     */
    public static String clean(String url) {
        if (!url.startsWith("http") && !url.startsWith("/")) {
            url = "/" + url;
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    /**
     * 按顺序连接给定的所有url
     *
     * @param uris 多个uri字符串
     * @return 按顺序连接好的url
     */
    public static String concat(String... uris) {
        StringBuilder sb = new StringBuilder();
        for (String uri : uris) {
            if (!uri.startsWith("http")) {
                if (!uri.startsWith("/")) {
                    sb.append('/');
                }
            }

            sb.append(uri);

            if (uri.endsWith("/")) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }

        return sb.toString();
    }
}
