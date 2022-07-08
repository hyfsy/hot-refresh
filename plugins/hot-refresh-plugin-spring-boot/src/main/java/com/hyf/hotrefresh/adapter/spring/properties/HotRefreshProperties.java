package com.hyf.hotrefresh.adapter.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author baB_hyf
 * @date 2022/05/13
 */
@ConfigurationProperties(HotRefreshProperties.PREFIX)
public class HotRefreshProperties {

    public static final String PREFIX = "hyf.hot-refresh";

    /**
     * 是否启用热刷新功能
     */
    private boolean enabled = true;

    /**
     * 针对SpringMVC提供的拦截器的相关配置
     */
    private final InterceptorEscape interceptorEscape = new InterceptorEscape();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public InterceptorEscape getInterceptorEscape() {
        return interceptorEscape;
    }

    public static class InterceptorEscape {

        /**
         * SpringMVC提供的拦截器是否进行跳过-全局配置
         */
        private boolean enabled = true;

        /**
         * 跳过的类
         */
        private List<Class<?>> includeClasses = new ArrayList<>();

        /**
         * 不跳过的类
         */
        private List<Class<?>> excludeClasses = new ArrayList<>();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<Class<?>> getIncludeClasses() {
            return includeClasses;
        }

        public void setIncludeClasses(List<Class<?>> includeClasses) {
            this.includeClasses = includeClasses;
        }

        public List<Class<?>> getExcludeClasses() {
            return excludeClasses;
        }

        public void setExcludeClasses(List<Class<?>> excludeClasses) {
            this.excludeClasses = excludeClasses;
        }
    }
}
