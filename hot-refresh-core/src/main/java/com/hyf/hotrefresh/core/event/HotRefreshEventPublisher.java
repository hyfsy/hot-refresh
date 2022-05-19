package com.hyf.hotrefresh.core.event;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class HotRefreshEventPublisher {

    private static HotRefreshEventPublisher publisher = new HotRefreshEventPublisher();

    private List<HotRefreshListener<?>> hotRefreshListeners = new ArrayList<>();

    private HotRefreshEventPublisher() {
        initListener();
    }

    public static HotRefreshEventPublisher getInstance() {
        return publisher;
    }

    public void publishEvent(HotRefreshEvent event) {
        hotRefreshListeners.forEach(l -> invokeListener(l, event));
    }

    private void initListener() {
        List<HotRefreshListener<?>> listeners = getHotRefreshListeners();
        hotRefreshListeners.addAll(listeners);
    }

    private List<HotRefreshListener<?>> getHotRefreshListeners() {
        List<HotRefreshListener<?>> list = new ArrayList<>();
        ServiceLoader<HotRefreshListener> listeners = ServiceLoader.load(HotRefreshListener.class);
        for (HotRefreshListener listener : listeners) {
            list.add(listener);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private void invokeListener(HotRefreshListener listener, HotRefreshEvent event) {
        listener.onRefreshEvent(event);
    }
}
