package com.hyf.hotrefresh.plugin.spring.config;

import com.hyf.hotrefresh.plugin.web.HotRefreshFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.Collections;

/**
 * TODO 仅支持boot环境
 *
 * @author baB_hyf
 * @date 2021/12/12
 */
@ConditionalOnClass({Filter.class, HotRefreshFilter.class})
public class HotRefreshFilterConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "hotRefreshFilter")
    public FilterRegistrationBean /* 兼容低版本spring */ hotRefreshFilter() {
        FilterRegistrationBean filterFilterRegistrationBean = new FilterRegistrationBean();
        filterFilterRegistrationBean.setFilter(getFilter());
        filterFilterRegistrationBean.setUrlPatterns(Collections.singletonList("/*"));
        filterFilterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        filterFilterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterFilterRegistrationBean;
    }

    protected Filter getFilter() {
        return new HotRefreshFilter();
    }
}
