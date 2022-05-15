package com.hyf.hotrefresh.core.memory;

import java.util.Map;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public interface ObfuscationHandler {

    Map<String, byte[]> handle(Map<String, byte[]> compiledBytes);

}
