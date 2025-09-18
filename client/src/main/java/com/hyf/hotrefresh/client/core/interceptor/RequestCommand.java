package com.hyf.hotrefresh.client.core.interceptor;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestCommand {

    private static String url;
    private static Map<String, String> params;
    private static Map<String, String> headers;
    private static Map<String, String> cookies;

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        RequestCommand.url = url;
    }

    public static Map<String, String> getParams() {
        if (params == null) {
            params = new LinkedHashMap<>();
        }
        return params;
    }

    public static Map<String, String> getHeaders() {
        if (headers == null) {
            headers = new LinkedHashMap<>();
        }
        return headers;
    }

    public static Map<String, String> getCookies() {
        if (cookies == null) {
            cookies = new LinkedHashMap<>();
        }
        return cookies;
    }
}
