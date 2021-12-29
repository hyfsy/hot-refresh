package com.hyf.hotrefresh;

import com.hyf.hotrefresh.agent.AgentHelper;
import com.hyf.hotrefresh.agent.ToolsJarProcessor;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class InfrastructureJarClassLoader extends URLClassLoader {

    private static final String BYTE_BUDDY_DOWNLOAD_URL = "https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy-agent/1.8.17/byte-buddy-agent-1.8.17.jar";
    private static final String ASM_DOWNLOAD_URL        = "https://repo1.maven.org/maven2/org/ow2/asm/asm/5.2/asm-5.2.jar";

    private static final String BYTE_BUDDY_LOCAL_PATH = "lib/byte-buddy-agent-1.8.17.jar";
    private static final String ASM_LOCAL_PATH        = "lib/asm-5.2.jar";
    private static final String JAVAC_LOCAL_PATH      = "lib/tools.jar";

    private static final String BYTE_BUDDY_AGENT_CLASS    = "net.bytebuddy.agent.ByteBuddyAgent";
    private static final String CLASS_READER_CLASS        = "org.objectweb.asm.ClassReader";
    private static final String JAVAC_TOOL_CLASS          = "com.sun.tools.javac.api.JavacTool";
    private static final String ATTACHMENT_PROVIDER_CLASS = "net.bytebuddy.agent.ByteBuddyAgent$AttachmentProvider";

    private static final InfrastructureJarClassLoader INSTANCE = newInstanceByLocal();

    private Class<?> agentClass              = null;
    private Class<?> attachmentProviderClass = null;
    private Method   installMethod           = null;
    private Class<?> classReaderClass        = null;
    private Method   getClassNameMethod      = null;

    private JavaCompiler compiler = null;

    private InfrastructureJarClassLoader(URL... urls) {
        super(urls, null);
        ensureByteBuddyExist();
        ensureAsmExist();
    }

    public static InfrastructureJarClassLoader getInstance() {
        return INSTANCE;
    }

    private static InfrastructureJarClassLoader newInstanceByLocal() {

        List<URL> urls = new ArrayList<>();
        ClassLoader ccl = Util.getOriginContextClassLoader();

        // byte-buddy
        URL byteBuddyResource = ccl.getResource(BYTE_BUDDY_LOCAL_PATH);
        URL byteBuddyURL = ResourceUtil.getResourceURL(byteBuddyResource);
        urls.add(byteBuddyURL);

        // asm
        URL asmResource = ccl.getResource(ASM_LOCAL_PATH);
        URL asmURL = ResourceUtil.getResourceURL(asmResource);
        urls.add(asmURL);

        // optional javac
        try {
            Class.forName(JAVAC_TOOL_CLASS, false, ccl);
        } catch (ClassNotFoundException ignored) {
            // TODO 先查询本地lib目录
            String toolsJarPath = new ToolsJarProcessor().getToolsJarPath();
            try {
                URL javacURL = ResourceUtil.getResourceURL(new File(toolsJarPath).toURI().toURL());
                urls.add(javacURL);
            } catch (MalformedURLException e) {
                Log.error("Failed to add javac source url", e);
            }
        }

        return new InfrastructureJarClassLoader(urls.toArray(new URL[0]));
    }

    public Instrumentation install() {
        Object attachmentProvider = AgentHelper.getAttachmentProvider();
        return invokeMethod(installMethod, null, attachmentProvider);
    }

    public String getClassName(byte[] bytes) {
        try {
            Object o = classReaderClass.getConstructor(byte[].class).newInstance((Object) bytes);
            String classNameWithPath = invokeMethod(getClassNameMethod, o);
            return classNameWithPath.replace("/", ".");
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to getClassName", e);
        }
    }

    public JavaCompiler getJavaCompiler() {
        if (compiler == null) {
            compiler = ToolProvider.getSystemJavaCompiler();

            // jre
            if (compiler == null) {
                Class<?> clazz = forName(JAVAC_TOOL_CLASS);
                Method createMethod = getMethod(clazz, "create");
                compiler = invokeMethod(createMethod, null);
            }
        }

        return compiler;
    }

    private void ensureByteBuddyExist() {
        agentClass = forName(BYTE_BUDDY_AGENT_CLASS);
        attachmentProviderClass = forName(ATTACHMENT_PROVIDER_CLASS);
        installMethod = getMethod(agentClass, "install", attachmentProviderClass);
    }

    private void ensureAsmExist() {
        classReaderClass = forName(CLASS_READER_CLASS);
        getClassNameMethod = getMethod(classReaderClass, "getClassName");
    }

    public Class<?> forName(String className) {
        Class<?> clazz;

        try {
            clazz = Class.forName(className, false, Util.getOriginContextClassLoader());
        } catch (ClassNotFoundException ignored) {
            try {
                clazz = Class.forName(className, false, this);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to get class: " + className, e);
            }
        }

        return clazz;
    }

    public Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to get field: " + clazz + "." + fieldName, e);
        }
    }

    public <T> T invokeField(Field field, Object obj) {
        try {
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to invoke field: " + field.getName(), e);
        }
    }

    public Method getMethod(Class<?> clazz, String methodName, Class<?>... args) {
        try {
            return clazz.getDeclaredMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to get method: " + clazz + "." + methodName, e);
        }
    }

    public <T> T invokeMethod(Method method, Object obj, Object... args) {
        try {
            method.setAccessible(true);
            return (T) method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to invoke " + method.getName(), e);
        }
    }
}
