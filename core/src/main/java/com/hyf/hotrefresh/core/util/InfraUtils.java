package com.hyf.hotrefresh.core.util;

import com.hyf.hotrefresh.common.util.FastReflectionUtils;
import com.hyf.hotrefresh.common.util.ReflectionUtils;
import com.hyf.hotrefresh.core.agent.AgentHelper;
import com.hyf.hotrefresh.core.agent.InstrumentationHolder;

import javax.tools.JavaCompiler;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public abstract class InfraUtils {

    private static final String BYTE_BUDDY_AGENT_CLASS    = "net.bytebuddy.agent.ByteBuddyAgent";
    private static final String ATTACHMENT_PROVIDER_CLASS = "net.bytebuddy.agent.ByteBuddyAgent$AttachmentProvider";
    private static final String CLASS_READER_CLASS        = "org.objectweb.asm.ClassReader";
    private static final String JAVAC_TOOL_CLASS          = "com.sun.tools.javac.api.JavacTool";

    private static Method   installMethod      = null;
    private static Class<?> classReaderClass   = null;
    private static Method   getClassNameMethod = null;

    private static volatile JavaCompiler    compiler                          = null;
    private static volatile Instrumentation instrumentation                   = null;
    private static volatile Instrumentation systemStartProcessInstrumentation = null;

    static {
        initByteBuddyEnvironment();
        initAsmEnvironment();
    }

    public static Instrumentation getInstrumentation() {
        // try {
        return getSystemStartProcessInstrumentation();
        // } catch (Throwable t) {
        //     return getDefaultInstrumentation();
        // }
    }

    /**
     * not recommend to use, just a intermediate object
     * <p>
     * this Instrumentation is a new one, not the one at jvm startup, may cause some potential problems
     *
     * @return agent attach generated instrumentation
     */
    public static Instrumentation getDefaultInstrumentation() {
        if (instrumentation == null) {
            try {
                Object attachmentProvider = AgentHelper.getAttachmentProvider();
                instrumentation = ReflectionUtils.invokeMethod(installMethod, null, attachmentProvider);
                AgentHelper.installSpringLoaded(instrumentation);
            } catch (Throwable t) {
                throw new IllegalStateException("Failed to get instrumentation", t);
            }
        }
        return instrumentation;
    }

    private static Instrumentation getSystemStartProcessInstrumentation() {
        if (systemStartProcessInstrumentation == null) {
            try {
                Class<?> clazz = InfraUtils.forName(InstrumentationHolder.class.getName());
                systemStartProcessInstrumentation = FastReflectionUtils.fastInvokeMethod(clazz, "getSystemStartProcessInstrumentation");
            } catch (Throwable t) {
                throw new IllegalStateException("Failed to get systemStartProcessInstrumentation", t);
            }
        }
        return systemStartProcessInstrumentation;
    }

    public static String getClassName(byte[] bytes) {
        try {
            Object o = classReaderClass.getConstructor(byte[].class).newInstance((Object) bytes);
            String classNameWithPath = ReflectionUtils.invokeMethod(getClassNameMethod, o);
            return classNameWithPath.replace("/", ".");
        } catch (Throwable t) {
            throw new IllegalStateException("Failed to get className", t);
        }
    }

    public static JavaCompiler getJavaCompiler() {
        if (compiler == null) {
            try {
                Class<?> clazz = forName(JAVAC_TOOL_CLASS);
                Method createMethod = ReflectionUtils.getMethod(clazz, "create");
                compiler = ReflectionUtils.invokeMethod(createMethod, null);
            } catch (Throwable t) {
                throw new IllegalStateException("Failed to get java compiler", t);
            }
        }

        return compiler;
    }

    public static boolean canLoad(Class<?> clazz) {
        ClassLoader cl = clazz.getClassLoader();

        // bootstrap class loader loaded
        if (cl == null) {
            return true;
        }

        ClassLoader p = Util.getInfrastructureJarClassLoader();
        while (p != null) {
            if (cl == p) {
                return true;
            }
            p = p.getParent();
        }

        return false;
    }

    private static void initByteBuddyEnvironment() {
        Class<?> agentClass = forName(BYTE_BUDDY_AGENT_CLASS);
        Class<?> attachmentProviderClass = forName(ATTACHMENT_PROVIDER_CLASS);
        installMethod = ReflectionUtils.getMethod(agentClass, "install", attachmentProviderClass);
    }

    private static void initAsmEnvironment() {
        classReaderClass = forName(CLASS_READER_CLASS);
        getClassNameMethod = ReflectionUtils.getMethod(classReaderClass, "getClassName");
    }

    public static Class<?> forName(String className) {
        return ReflectionUtils.forName(className, Util.getInfrastructureJarClassLoader());
    }
}
