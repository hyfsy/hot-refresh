package com.hyf.hotrefresh;

import com.hyf.hotrefresh.memory.MemoryClassLoader;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class Util {

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

    public static MemoryClassLoader getThrowawayMemoryClassLoader() {
        return MemoryClassLoader.newInstance();
    }

    public static InfrastructureJarClassLoader getInfrastructureJarClassLoader() {
        return InfrastructureJarClassLoader.getInstance();
    }
}
