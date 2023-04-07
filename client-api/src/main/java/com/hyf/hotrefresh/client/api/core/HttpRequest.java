package com.hyf.hotrefresh.client.api.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2023/04/06
 */
public class HttpRequest {

    private String              url;
    private Map<String, String> params;
    private Map<String, String> headers;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
        }
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
