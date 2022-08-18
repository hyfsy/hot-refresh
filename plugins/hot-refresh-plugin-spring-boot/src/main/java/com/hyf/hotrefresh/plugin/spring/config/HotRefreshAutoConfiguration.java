package com.hyf.hotrefresh.plugin.spring.config;

import com.hyf.hotrefresh.plugin.spring.condition.HotRefreshEnableCondition;
import com.hyf.hotrefresh.plugin.spring.properties.HotRefreshProperties;
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
@ConditionalOnProperty(prefix = HotRefreshProperties.PREFIX, name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(HotRefreshProperties.class)
@Import({HotRefreshFilterConfiguration.class, MappedInterceptorEscapeConfiguration.class, HotRefreshRpcServerConfiguration.class})
public class HotRefreshAutoConfiguration {

}
