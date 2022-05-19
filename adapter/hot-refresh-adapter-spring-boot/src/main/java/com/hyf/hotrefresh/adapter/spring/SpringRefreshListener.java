package com.hyf.hotrefresh.adapter.spring;

import com.hyf.hotrefresh.core.event.ByteCodeRefreshedEvent;
import com.hyf.hotrefresh.core.event.HotRefreshEvent;
import com.hyf.hotrefresh.core.event.HotRefreshListener;

/**
 * @author baB_hyf
 * @date 2022/05/19
 */
public class SpringRefreshListener implements HotRefreshListener<HotRefreshEvent> {

    @Override
    public void onRefreshEvent(HotRefreshEvent event) {

        if (event instanceof ByteCodeRefreshedEvent) {

        }
    }
}
