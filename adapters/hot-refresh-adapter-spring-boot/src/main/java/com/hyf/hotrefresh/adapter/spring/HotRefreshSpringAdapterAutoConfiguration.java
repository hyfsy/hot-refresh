package com.hyf.hotrefresh.adapter.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 应用上下文工具
 *
 * @author baB_hyf
 * @date 2022/05/19
 */
@Configuration
public class HotRefreshSpringAdapterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "hotRefreshApplicationContextUtils")
    public ApplicationContextUtils hotRefreshApplicationContextUtils() {
        return new ApplicationContextUtils();
    }
}
