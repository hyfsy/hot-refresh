package com.hyf.hotrefresh.plugin.spring.config;

import com.hyf.hotrefresh.plugin.spring.properties.HotRefreshProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.handler.MappedInterceptor;

@ConditionalOnProperty(prefix = HotRefreshProperties.PREFIX, name = "interceptor-escape.enabled", matchIfMissing = true)
@ConditionalOnClass(MappedInterceptor.class)
public class MappedInterceptorEscapeConfiguration {

    @Bean
    public MappedInterceptorEscapeHelper mappedInterceptorEscapeHelper() {
        return new MappedInterceptorEscapeHelper();
    }

}
