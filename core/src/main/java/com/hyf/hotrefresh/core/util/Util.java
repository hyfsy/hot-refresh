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
        if (ccl == null) {
            return null;
        }

        ClassLoader tmp = ccl;
        do {
            if (tmp instanceof MemoryClassLoader) {
                return tmp.getParent();
            }
        }
        while ((tmp = tmp.getParent()) != null);

        return ccl;
    }

    /**
     * 注意避免内存泄漏的问题，结合tomcat的{@link org.apache.commons.logging.LogFactory#cacheFactory(ClassLoader, LogFactory)}缓存功能
     */
    public static HotRefreshClassLoader getThrowawayHotRefreshClassLoader() {
        return HotRefreshClassLoader.newInstance();
    }

    public static HotRefreshClassLoader getHotRefreshClassLoader() {
        return HotRefreshClassLoader.getInstance();
    }

    public static InfrastructureJarClassLoader getInfrastructureJarClassLoader() {
        return InfrastructureJarClassLoader.getInstance();
    }
}
