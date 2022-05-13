package com.hyf.hotrefresh.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author baB_hyf
 * @date 2022/05/13
 */
@Configuration
@Import(HotRefreshFilterConfiguration.class)
public class HotRefreshAutoConfiguration {

    @Bean
    public HotRefreshInstaller springInstall() {
        return new HotRefreshInstaller();
    }
}
