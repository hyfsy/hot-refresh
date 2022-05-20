package com.hyf.hotrefresh.adapter.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author baB_hyf
 * @date 2022/05/13
 */
@ConfigurationProperties("hyf.hot-refresh")
public class HotRefreshProperties {

    /**
     * 是否启用热刷新功能
     */
    public boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}