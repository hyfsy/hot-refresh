package com.hyf.hotrefresh.adapter.spring.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.handler.MappedInterceptor;

@ConditionalOnClass(MappedInterceptor.class)
public class MappedInterceptorEscapeConfiguration {

    @Bean
    public MappedInterceptorEscapeHelper mappedInterceptorEscapeHelper() {
        return new MappedInterceptorEscapeHelper();
    }

}
