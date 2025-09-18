package com.hyf.hotrefresh.client.core.interceptor;

import com.hyf.hotrefresh.client.api.core.HttpRequest;
import com.hyf.hotrefresh.client.api.core.RequestInterceptor;

/**
 * @author baB_hyf
 * @date 2023/04/07
 */
public class CommandRequestInterceptor implements RequestInterceptor {

    @Override
    public void intercept(HttpRequest request) {
        if (RequestCommand.getUrl() != null) {
            request.setUrl(RequestCommand.getUrl());
        }
        if (!RequestCommand.getParams().isEmpty()) {
            request.getParams().putAll(RequestCommand.getParams());
        }
        if (!RequestCommand.getHeaders().isEmpty()) {
            request.getHeaders().putAll(RequestCommand.getHeaders());
        }
        if (!RequestCommand.getCookies().isEmpty()) {
            request.getCookies().putAll(RequestCommand.getCookies());
        }
    }

}
