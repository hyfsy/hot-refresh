package com.hyf.hotrefresh.refresh;

import com.hyf.hotrefresh.util.Util;
import com.hyf.hotrefresh.exception.AgentException;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
class HotRefreshManager {

    private static final HotRefreshTransformer hotRefreshTransformer = new HotRefreshTransformer(Util.getThrowawayMemoryClassLoader());

    private static final Instrumentation INST;

    static {
        INST = Util.getInstrumentation();
        start();
        // Log.info("Hot refresh infrastructure has been installed");
    }

    public static Instrumentation getInstrumentation() {
        if (INST == null) {
            throw new IllegalStateException("Instrumentation must not be null");
        }
        return INST;
    }

    //---------------------------------------------------------------------
    // 针对于SpringLoaded的情况，处理方法已无效
    //---------------------------------------------------------------------

    @Deprecated
    public static void start() {
        stop();
        getInstrumentation().addTransformer(hotRefreshTransformer, true);
    }

    @Deprecated
    public static void stop() {
        getInstrumentation().removeTransformer(hotRefreshTransformer);
    }

    @Deprecated
    public static void reset(String className) throws AgentException {
        Class<?> clazz = Util.getThrowawayMemoryClassLoader().remove(className);
        reTransform(clazz);
    }

    @Deprecated
    public static void resetAll() throws AgentException {
        List<Class<?>> classList = Util.getThrowawayMemoryClassLoader().clear();
        reTransform(classList.toArray(new Class[0]));
    }

    public static void reTransform(Class<?>... classes) throws AgentException {
        if (classes.length == 0) {
            return;
        }

        try {
            getInstrumentation().retransformClasses(classes);
        } catch (Throwable e) {
            String classNames = Arrays.stream(classes).map(Class::getName).collect(Collectors.joining("; "));
            throw new AgentException("Class file structure has been modified: " + classNames, e);
        }
    }
}
