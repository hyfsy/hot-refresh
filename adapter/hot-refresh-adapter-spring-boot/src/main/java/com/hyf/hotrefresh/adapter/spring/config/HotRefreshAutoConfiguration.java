package com.hyf.hotrefresh.adapter.spring.config;

import com.hyf.hotrefresh.adapter.spring.properties.HotRefreshProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author baB_hyf
 * @date 2022/05/13
 */
@Configuration
@Conditional(HotRefreshEnableCondition.class)
@ConditionalOnProperty(prefix = "hyf.hot-refresh", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(HotRefreshProperties.class)
@Import(HotRefreshFilterConfiguration.class)
public class HotRefreshAutoConfiguration {

}
