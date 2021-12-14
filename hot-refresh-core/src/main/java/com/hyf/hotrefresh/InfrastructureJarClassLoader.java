package com.hyf.hotrefresh;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class InfrastructureJarClassLoader extends URLClassLoader {

    private static final String BYTE_BUDDY_DOWNLOAD_URL = "https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy-agent/1.8.17/byte-buddy-agent-1.8.17.jar";
    private static final String ASM_DOWNLOAD_URL        = "https://repo1.maven.org/maven2/org/ow2/asm/asm/5.2/asm-5.2.jar";

    private static final URL byteBuddyResource = Util.getOriginContextClassLoader().getResource("lib/byte-buddy-agent-1.8.17.jar");
    private static final URL asmResource       = Util.getOriginContextClassLoader().getResource("lib/asm-5.2.jar");

    private static final String BYTE_BUDDY_AGENT_CLASS = "net.bytebuddy.agent.ByteBuddyAgent";
    private static final String CLASS_READER_CLASS     = "org.objectweb.asm.ClassReader";

    private static final InfrastructureJarClassLoader INSTANCE = new InfrastructureJarClassLoader(byteBuddyResource, asmResource);

    private Class<?> agentClass         = null;
    private Method   installMethod      = null;
    private Class<?> classReaderClass   = null;
    private Method   getClassNameMethod = null;

    private InfrastructureJarClassLoader(URL... urls) {
        super(urls, Util.getOriginContextClassLoader());
        ensureByteBuddyExist();
        ensureAsmExist();
    }

    public static InfrastructureJarClassLoader getInstance() {
        return INSTANCE;
    }

    public static InfrastructureJarClassLoader newInstanceByNetwork() {
        try {
            URL byteBuddyUrl = new URL(BYTE_BUDDY_DOWNLOAD_URL);
            URL asmUrl = new URL(ASM_DOWNLOAD_URL);
            return new InfrastructureJarClassLoader(byteBuddyUrl, asmUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Get ByteBuddyJarClassLoader failed", e);
        }
    }

    public String getClassName(byte[] bytes) {
        try {
            Object o = classReaderClass.getConstructor(byte[].class).newInstance((Object) bytes);
            String classNameWithPath = (String) getClassNameMethod.invoke(o);
            return classNameWithPath.replace("/", ".");
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to getClassName", e);
        }
    }

    public Instrumentation install() {
        try {
            return (Instrumentation) installMethod.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to install", e);
        }
    }

    private void ensureByteBuddyExist() {
        agentClass = forName(BYTE_BUDDY_AGENT_CLASS);
        installMethod = getMethod(agentClass, "install");
    }

    private void ensureAsmExist() {
        classReaderClass = forName(CLASS_READER_CLASS);
        getClassNameMethod = getMethod(classReaderClass, "getClassName");
    }

    private Method getMethod(Class<?> clazz, String methodName, Class<?>... args) {
        try {
            return clazz.getDeclaredMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to get method: " + clazz + "#" + methodName, e);
        }
    }

    private Class<?> forName(String className) {
        Class<?> clazz;

        try {
            clazz = Class.forName(className, false, Util.getOriginContextClassLoader());
        } catch (ClassNotFoundException ignored) {
            try {
                clazz = this.findClass(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to class: " + className, e);
            }
        }

        return clazz;
    }
}
