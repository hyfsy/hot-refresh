package com.hyf.hotrefresh.adapter.spring;

import com.hyf.hotrefresh.adapter.spring.copy.SpringAgent;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.core.event.ByteCodeRefreshedEvent;
import com.hyf.hotrefresh.core.event.HotRefreshListener;

import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/19
 */
public class SpringHotRefreshListener implements HotRefreshListener<ByteCodeRefreshedEvent> {

    @Override
    public void onRefreshEvent(ByteCodeRefreshedEvent event) {
        Map<String, byte[]> compiledBytes = event.getCompiledBytes();
        compiledBytes.forEach(this::refreshClass);
    }

    private void refreshClass(String name, byte[] bytes) {
        try {
            SpringAgent.refreshClass(name, bytes);
        } catch (Throwable t) {
            if (Log.isDebugMode()) {
                Log.error("Failed to refresh spring class", t);
            }
        }
    }

}
