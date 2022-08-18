package com.hyf.hotrefresh.core.refresh;

import com.hyf.hotrefresh.core.agent.AgentHelper;
import com.hyf.hotrefresh.core.exception.AgentException;
import com.hyf.hotrefresh.core.util.Util;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
class HotRefreshManager {

    // so we broken the parent delegate to fixed it, you can see [d1333eaf](https://github.com/hyfsy/hot-refresh/commit/d1333eaff0e03fb4ef2903c28d8013d5f6662127) for more details.
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

    public static void start() {
        stop();
        getInstrumentation().addTransformer(hotRefreshTransformer, true);
        // set the hotrefresh transformer before the skywalking agent transformer
        // AgentHelper.setTransformerToFirst(getInstrumentation(), hotRefreshTransformer, true);
    }

    public static void stop() {
        getInstrumentation().removeTransformer(hotRefreshTransformer);
    }

    public static void reTransform(Class<?>... classes) throws AgentException {
        if (classes.length == 0) {
            return;
        }

        try {
            getInstrumentation().retransformClasses(classes);
        } catch (Throwable e) {
            String classNames = Arrays.stream(classes).map(Class::getName).collect(Collectors.joining("; "));
            String reTransformFailedBytesMessage = ReTransformExceptionRecorder.buildBytesMessage();
            String agentExceptionMessage = "Class file structure has been modified: " + classNames + reTransformFailedBytesMessage;
            throw new AgentException(agentExceptionMessage, e);
        } finally {
            ReTransformExceptionRecorder.clear();
        }
    }

    //---------------------------------------------------------------------
    // 针对于SpringLoaded的情况，处理方法已无效
    //---------------------------------------------------------------------

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
}
