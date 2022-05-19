package com.hyf.hotrefresh.core.event;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public interface HotRefreshListener<T extends HotRefreshEvent> {

    void onRefreshEvent(T event);
}
