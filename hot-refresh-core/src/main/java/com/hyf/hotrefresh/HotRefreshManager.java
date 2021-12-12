package com.hyf.hotrefresh;

import com.hyf.hotrefresh.exception.AgentException;
import com.hyf.hotrefresh.memory.MemoryClassLoader;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.List;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class HotRefreshManager {

    private static final HotRefreshTransformer HOT_REFRESH_TRANSFORMER = new HotRefreshTransformer(Util.getThrowawayMemoryClassLoader());

    private static final Instrumentation INST;

    static {
        INST = Util.getInfrastructureJarClassLoader().install();
        start();
        System.out.println("Hot refresh infrastructure has been installed");
    }

    public static Instrumentation getInstrumentation() {
        return INST;
    }

    public static void start() {
        INST.addTransformer(HOT_REFRESH_TRANSFORMER, true);
    }

    public static void stop() {
        INST.removeTransformer(HOT_REFRESH_TRANSFORMER);
    }

    public static void reset(String className) throws AgentException {
        Class<?> clazz;
        try {
            MemoryClassLoader mcl = Util.getThrowawayMemoryClassLoader();
            clazz = mcl.loadClass(className);
            mcl.remove(className);
        } catch (ClassNotFoundException e) {
            throw new AgentException("Failed to reset class: " + className, e);
        }

        try {
            HotRefreshManager.getInstrumentation().retransformClasses(clazz);
        } catch (UnmodifiableClassException e) {
            throw new AgentException("Failed to reset classes", e);
        }
    }

    public static void resetAll() throws AgentException {
        List<Class<?>> classList = Util.getThrowawayMemoryClassLoader().clear();
        try {
            HotRefreshManager.getInstrumentation().retransformClasses(classList.toArray(new Class[0]));
        } catch (UnmodifiableClassException e) {
            throw new AgentException("Failed to reTransform classes", e);
        }
    }
}
