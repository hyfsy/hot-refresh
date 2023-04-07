package com.hyf.hotrefresh.client.api.core;

/**
 * @author baB_hyf
 * @date 2023/04/06
 */
public interface RequestInterceptor {

    void intercept(HttpRequest request);

}
