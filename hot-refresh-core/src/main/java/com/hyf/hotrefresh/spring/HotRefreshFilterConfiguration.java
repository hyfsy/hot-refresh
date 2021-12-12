package com.hyf.hotrefresh.spring;

import com.hyf.hotrefresh.servlet.HotRefreshFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.Collections;

/**
 * TODO 仅支持boot环境
 *
 * @author baB_hyf
 * @date 2021/12/12
 */
@Configuration
public class HotRefreshFilterConfiguration {

    @Bean
    public FilterRegistrationBean<Filter> hotRefreshFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new HotRefreshFilter());
        filterFilterRegistrationBean.setUrlPatterns(Collections.singletonList("/*"));
        filterFilterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        return filterFilterRegistrationBean;
    }
}
