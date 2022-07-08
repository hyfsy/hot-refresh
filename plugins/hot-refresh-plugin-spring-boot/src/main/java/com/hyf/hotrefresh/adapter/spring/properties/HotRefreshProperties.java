package com.hyf.hotrefresh.adapter.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

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
    public boolean enabled = true;

    /**
     * 针对SpringMVC提供的拦截器的相关配置
     */
    public final InterceptorEscape interceptorEscape = new InterceptorEscape();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public InterceptorEscape getInterceptorEscape() {
        return interceptorEscape;
    }

    public static final class InterceptorEscape {

        /**
         * SpringMVC提供的拦截器是否进行跳过-全局配置
         */
        public boolean enabled = true;

        /**
         * 跳过的类
         */
        public List<Class<?>> includeClasses;

        /**
         * 不跳过的类
         */
        public List<Class<?>> excludeClasses;

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
