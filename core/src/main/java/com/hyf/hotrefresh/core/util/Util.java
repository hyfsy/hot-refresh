package com.hyf.hotrefresh.core.util;

import com.hyf.hotrefresh.core.classloader.InfrastructureJarClassLoader;
import com.hyf.hotrefresh.core.refresh.HotRefreshClassLoader;
import com.hyf.hotrefresh.core.memory.MemoryClassLoader;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public abstract class Util {

    public static ClassLoader getOriginContextClassLoader() {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();

        ClassLoader tmp = ccl;
        do {
            if (tmp instanceof MemoryClassLoader) {
                return tmp.getParent();
            }
        }
        while ((tmp = tmp.getParent()) != null);

        return ccl;
    }

    public static HotRefreshClassLoader getThrowawayHotRefreshClassLoader() {
        return HotRefreshClassLoader.newInstance();
    }

    public static InfrastructureJarClassLoader getInfrastructureJarClassLoader() {
        return InfrastructureJarClassLoader.getInstance();
    }
}
