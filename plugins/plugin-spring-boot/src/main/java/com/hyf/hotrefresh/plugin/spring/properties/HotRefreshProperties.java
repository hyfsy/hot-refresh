package com.hyf.hotrefresh.plugin.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 热刷新配置
 *
 * @author baB_hyf
 * @date 2022/05/13
 */
@ConfigurationProperties(HotRefreshProperties.PREFIX)
public class HotRefreshProperties {

    public static final String PREFIX = "hyf.hot-refresh";

    /**
     * 针对SpringMVC提供的拦截器的相关配置
     */
    private final InterceptorEscape interceptorEscape = new InterceptorEscape();

    /**
     * 热刷新rpc服务配置
     */
    private final Server server = new Server();

    /**
     * 是否启用热刷新功能
     */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public InterceptorEscape getInterceptorEscape() {
        return interceptorEscape;
    }

    public Server getServer() {
        return server;
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

    public static class Server {

        /**
         * 是否启用内置rpc服务
         */
        private boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
