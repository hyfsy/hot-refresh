package com.hyf.hotrefresh.plugin.spring.config;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.util.FastReflectionUtils;
import com.hyf.hotrefresh.core.util.InfraUtils;
import com.hyf.hotrefresh.plugin.spring.agent.MappedInterceptorEscape;

import javax.annotation.PostConstruct;

public class MappedInterceptorEscapeHelper {

    @PostConstruct
    public void post() {
        try {
            Class<?> MappedInterceptorEscapeClass = InfraUtils.forName(MappedInterceptorEscape.class.getName());
            FastReflectionUtils.fastInvokeMethod(MappedInterceptorEscapeClass, "escapeMappedInterceptor");
        } catch (Throwable t) {
            Log.error("Failed to escapeMappedInterceptor", t);
        }
    }
}
