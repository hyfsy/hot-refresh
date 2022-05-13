package com.hyf.hotrefresh.adapter.spring.config;

import com.hyf.hotrefresh.adapter.web.HotRefreshFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.Collections;

/**
 * TODO 仅支持boot环境
 *
 * @author baB_hyf
 * @date 2021/12/12
 */
public class HotRefreshFilterConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean /* 兼容低版本spring */ hotRefreshFilter() {
        FilterRegistrationBean filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(getFilter());
        filterFilterRegistrationBean.setUrlPatterns(Collections.singletonList("/*"));
        filterFilterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        return filterFilterRegistrationBean;
    }

    protected Filter getFilter() {
        return new HotRefreshFilter();
    }
}
