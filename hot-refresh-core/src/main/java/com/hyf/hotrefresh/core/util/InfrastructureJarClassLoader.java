package com.hyf.hotrefresh.core.util;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.util.ReflectUtils;
import com.hyf.hotrefresh.core.agent.AgentHelper;
import com.hyf.hotrefresh.core.agent.ToolsJarProcessor;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class InfrastructureJarClassLoader extends URLClassLoader {

    // TODO 支持外部指定
    private static final String BYTE_BUDDY_LOCAL_PATH = "lib/byte-buddy-agent-1.8.17.jar";
    private static final String ASM_LOCAL_PATH        = "lib/asm-5.2.jar";

    private static final String BYTE_BUDDY_AGENT_CLASS    = "net.bytebuddy.agent.ByteBuddyAgent";
    private static final String CLASS_READER_CLASS        = "org.objectweb.asm.ClassReader";
    private static final String JAVAC_TOOL_CLASS          = "com.sun.tools.javac.api.JavacTool";
    private static final String ATTACHMENT_PROVIDER_CLASS = "net.bytebuddy.agent.ByteBuddyAgent$AttachmentProvider";

    private static final InfrastructureJarClassLoader INSTANCE = newInstanceByLocal();

    private static final Map<String, String> registeredInfrastructureJarMap = new ConcurrentHashMap<>(4);

    private Class<?> agentClass              = null;
    private Class<?> attachmentProviderClass = null;
    private Method   installMethod           = null;
    private Class<?> classReaderClass        = null;
    private Method   getClassNameMethod      = null;

    private JavaCompiler    compiler        = null;
    private Instrumentation instrumentation = null;

    private InfrastructureJarClassLoader(URL... urls) {
        // TODO 是否会因为ccl变化而产生问题？
        super(urls, Util.getOriginContextClassLoader());
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

        // optional tools
        try {
            Class.forName(JAVAC_TOOL_CLASS, false, ccl);
        } catch (ClassNotFoundException ignored) {
            // TODO 先查询本地lib目录
            String toolsJarPath = new ToolsJarProcessor().getToolsJarPath();
            if (toolsJarPath != null) {
                try {
                    URL javacURL = ResourceUtil.getResourceURL(new File(toolsJarPath).toURI().toURL());
                    urls.add(javacURL);
                } catch (MalformedURLException e) {
                    Log.error("Failed to add javac source url", e);
                }
            }
        }

        return new InfrastructureJarClassLoader(urls.toArray(new URL[0]));
    }

    // TODO 不仅jar，还要class
    public void registerInfrastructureJar(String identity, String location) {

        registeredInfrastructureJarMap.put(identity, location);

        URL resource = transferToResourceURL(location);
        if (resource == null) {
            Log.warn("Failed to register infrastructure jar: " + location);
            return;
        }
        URL url = ResourceUtil.getResourceURL(resource);
        // TODO 是否替换原有的？如何替换？
        super.addURL(url);
    }

    private URL transferToResourceURL(String location) {
        URL resource = null;
        if (location.startsWith("http")) {
            try {
                resource = new URL(location);
            } catch (MalformedURLException ignore) {
            }
        }
        else {
            resource = Util.getOriginContextClassLoader().getResource(location);
        }

        return resource;
    }

    public Instrumentation getInstrumentation() {
        if (instrumentation == null) {
            Object attachmentProvider = AgentHelper.getAttachmentProvider();
            instrumentation = ReflectUtils.invokeMethod(installMethod, null, attachmentProvider);
            AgentHelper.installSpringLoaded(instrumentation);
        }
        return instrumentation;
    }

    public String getClassName(byte[] bytes) {
        try {
            Object o = classReaderClass.getConstructor(byte[].class).newInstance((Object) bytes);
            String classNameWithPath = ReflectUtils.invokeMethod(getClassNameMethod, o);
            return classNameWithPath.replace("/", ".");
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to getClassName", e);
        }
    }

    public JavaCompiler getJavaCompiler() {
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
                    Method createMethod = ReflectUtils.getMethod(clazz, "create");
                    compiler = ReflectUtils.invokeMethod(createMethod, null);
                } catch (Throwable ignored) {
                }
            }
        }

        return compiler;
    }

    public boolean canLoad(Class<?> clazz) {
        ClassLoader cl = clazz.getClassLoader();

        // bootstrap class loader loaded
        if (cl == null) {
            return true;
        }

        ClassLoader p = this;
        while (p != null) {
            if (cl == p) {
                return true;
            }
            p = p.getParent();
        }

        return false;
    }

    private void ensureByteBuddyExist() {
        agentClass = forName(BYTE_BUDDY_AGENT_CLASS);
        attachmentProviderClass = forName(ATTACHMENT_PROVIDER_CLASS);
        installMethod = ReflectUtils.getMethod(agentClass, "install", attachmentProviderClass);
    }

    private void ensureAsmExist() {
        classReaderClass = forName(CLASS_READER_CLASS);
        getClassNameMethod = ReflectUtils.getMethod(classReaderClass, "getClassName");
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
}
