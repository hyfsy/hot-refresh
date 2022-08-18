package com.hyf.hotrefresh.core.event;

import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class ByteCodeRefreshedEvent extends HotRefreshEvent {

    public ByteCodeRefreshedEvent(Map<String, byte[]> compiledBytes) {
        super(compiledBytes);
    }

    public Map<String, byte[]> getCompiledBytes() {
        return (Map<String, byte[]>) this.getSource();
    }
}
