package com.hyf.hotrefresh.client.api.plugin;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public interface Plugin {

    void install() throws Exception;

    void uninstall();
}
