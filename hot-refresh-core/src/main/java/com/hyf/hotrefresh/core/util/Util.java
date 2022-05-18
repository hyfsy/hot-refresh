package com.hyf.hotrefresh.core.util;

import com.hyf.hotrefresh.core.memory.MemoryClassLoader;

import java.lang.instrument.Instrumentation;

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

    public static MemoryClassLoader getThrowawayMemoryClassLoader() {
        return MemoryClassLoader.newInstance();
    }

    public static InfrastructureJarClassLoader getInfrastructureJarClassLoader() {
        return InfrastructureJarClassLoader.getInstance();
    }

    public static Instrumentation getInstrumentation() {
        try {
            return getInfrastructureJarClassLoader().getInstrumentation();
        } catch (Throwable e) {
            // throw new IllegalStateException(e);
            return null;
        }
    }
}
