package com.hyf.hotrefresh.core.event;

import com.hyf.hotrefresh.common.util.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class HotRefreshEventPublisher {

    private static HotRefreshEventPublisher publisher = new HotRefreshEventPublisher();

    private List<HotRefreshListener<? extends HotRefreshEvent>> hotRefreshListeners = new ArrayList<>();

    private Map<HotRefreshListener<? extends HotRefreshEvent>, Class<? extends HotRefreshEvent>> supportCache = new ConcurrentHashMap<>();

    private HotRefreshEventPublisher() {
        initListener();
    }

    public static HotRefreshEventPublisher getInstance() {
        return publisher;
    }

    public void publishEvent(HotRefreshEvent event) {
        hotRefreshListeners.stream().filter(l -> supportsEvent(l, event.getClass())).forEach(l -> invokeListener(l, event));
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

    @SuppressWarnings("uncheked")
    protected boolean supportsEvent(HotRefreshListener<? extends HotRefreshEvent> listener, Class<? extends HotRefreshEvent> eventType) {
        Class<? extends HotRefreshEvent> supportEventType = supportCache.compute(listener, (l, c) -> {

            if (c == null) {
                List<ParameterizedType> allGenericTypes = TypeUtils.getAllGenericTypes(listener.getClass());
                for (ParameterizedType parameterizedType : allGenericTypes) {
                    Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
                    if (actualTypeArgument instanceof Class) { // 泛型是个Class，表示固定的
                        c = (Class<? extends HotRefreshEvent>)actualTypeArgument;
                    }
                }
            }

            return c;
        });

        return supportEventType != null && supportEventType.isAssignableFrom(eventType);
    }

    @SuppressWarnings("unchecked")
    private void invokeListener(HotRefreshListener listener, HotRefreshEvent event) {
        listener.onRefreshEvent(event);
    }
}
