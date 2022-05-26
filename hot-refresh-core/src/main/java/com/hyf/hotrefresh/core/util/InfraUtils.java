package com.hyf.hotrefresh.core.util;

import com.hyf.hotrefresh.common.util.ReflectionUtils;
import com.hyf.hotrefresh.core.agent.AgentHelper;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public abstract class InfraUtils {

    private static final String BYTE_BUDDY_AGENT_CLASS    = "net.bytebuddy.agent.ByteBuddyAgent";
    private static final String CLASS_READER_CLASS        = "org.objectweb.asm.ClassReader";
    private static final String JAVAC_TOOL_CLASS          = "com.sun.tools.javac.api.JavacTool";
    private static final String ATTACHMENT_PROVIDER_CLASS = "net.bytebuddy.agent.ByteBuddyAgent$AttachmentProvider";

    private static Class<?> agentClass              = null;
    private static Class<?> attachmentProviderClass = null;
    private static Method   installMethod           = null;
    private static Class<?> classReaderClass        = null;
    private static Method   getClassNameMethod      = null;

    private static JavaCompiler    compiler        = null;
    private static Instrumentation instrumentation = null;

    private InfraUtils() {
        ensureByteBuddyExist();
        ensureAsmExist();
    }

    public static Instrumentation getInstrumentation() {
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

    public static String getClassName(byte[] bytes) {
        try {
            Object o = classReaderClass.getConstructor(byte[].class).newInstance((Object) bytes);
            String classNameWithPath = ReflectionUtils.invokeMethod(getClassNameMethod, o);
            return classNameWithPath.replace("/", ".");
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to getClassName", e);
        }
    }

    public static JavaCompiler getJavaCompiler() {
        if (compiler == null) {
            compiler = ToolProvider.getSystemJavaCompiler();
            // if tools.jar not on the classpath then default to use URLClassLoader$FactoryURLClassLoader load
            if (compiler != null && compiler.getClass().getClassLoader() != ClassLoader.getSystemClassLoader()) {
                // use our class loader to load to avoid ClassNotFoundException at compile phase
                compiler = null;
            }

            // jre
            if (compiler == null) {
                try {
                    Class<?> clazz = forName(JAVAC_TOOL_CLASS);
                    Method createMethod = ReflectionUtils.getMethod(clazz, "create");
                    compiler = ReflectionUtils.invokeMethod(createMethod, null);
                } catch (Throwable ignored) {
                }
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

    private static void ensureByteBuddyExist() {
        agentClass = forName(BYTE_BUDDY_AGENT_CLASS);
        attachmentProviderClass = forName(ATTACHMENT_PROVIDER_CLASS);
        installMethod = ReflectionUtils.getMethod(agentClass, "install", attachmentProviderClass);
    }

    private static void ensureAsmExist() {
        classReaderClass = forName(CLASS_READER_CLASS);
        getClassNameMethod = ReflectionUtils.getMethod(classReaderClass, "getClassName");
    }

    public static Class<?> forName(String className) {
        return ReflectionUtils.forName(className, Util.getInfrastructureJarClassLoader());
    }
}
