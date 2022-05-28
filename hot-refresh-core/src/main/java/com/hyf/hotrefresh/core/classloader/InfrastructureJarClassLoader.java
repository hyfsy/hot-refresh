package com.hyf.hotrefresh.core.classloader;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.core.agent.ToolsJarProcessor;
import com.hyf.hotrefresh.core.util.ResourceUtils;
import com.hyf.hotrefresh.core.util.Util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class InfrastructureJarClassLoader extends ExtendClassLoader {

    // TODO 支持外部指定
    private static final String BYTE_BUDDY_LOCAL_PATH = "lib/byte-buddy-agent-1.8.17.jar";
    private static final String ASM_LOCAL_PATH        = "lib/asm-5.2.jar";

    private static final String JAVAC_TOOL_CLASS = "com.sun.tools.javac.api.JavacTool";

    private static final InfrastructureJarClassLoader INSTANCE = newInstanceByLocal();

    private static final Map<String, String> registeredInfrastructureJarMap = new ConcurrentHashMap<>(4);

    private final Map<String, Class<?>> loadedClass = new ConcurrentHashMap<>();

    private InfrastructureJarClassLoader(URL... urls) {
        // TODO 是否会因为ccl变化而产生问题？
        super(urls, Util.getOriginContextClassLoader());
    }

    public static InfrastructureJarClassLoader getInstance() {
        return INSTANCE;
    }

    private static InfrastructureJarClassLoader newInstanceByLocal() {
        URL[] urls = getDefaultInfrastructureURLs();
        return new InfrastructureJarClassLoader(urls);
    }

    private static URL[] getDefaultInfrastructureURLs() {
        List<URL> urls = new ArrayList<>();
        ClassLoader ccl = Util.getOriginContextClassLoader();

        // byte-buddy
        URL byteBuddyResource = ccl.getResource(BYTE_BUDDY_LOCAL_PATH);
        URL byteBuddyURL = ResourceUtils.getResourceURL(byteBuddyResource);
        urls.add(byteBuddyURL);

        // asm
        URL asmResource = ccl.getResource(ASM_LOCAL_PATH);
        URL asmURL = ResourceUtils.getResourceURL(asmResource);
        urls.add(asmURL);

        // tools
        String toolsJarPath = new ToolsJarProcessor().getToolsJarPath();
        if (toolsJarPath != null) {
            try {
                URL javacURL = ResourceUtils.getResourceURL(new File(toolsJarPath).toURI().toURL());
                urls.add(javacURL);
            } catch (MalformedURLException e) {
                Log.error("Failed to add javac source url", e);
            }
        }

        return urls.toArray(new URL[0]);
    }

    // TODO 不仅jar，还要class
    public void registerInfrastructureJar(String identity, String location) {

        registeredInfrastructureJarMap.put(identity, location);

        URL resource = transferToResourceURL(location);
        if (resource == null) {
            Log.warn("Failed to register infrastructure jar: " + location);
            return;
        }
        URL url = ResourceUtils.getResourceURL(resource);
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

    @Override
    protected Class<?> brokenLoadClass(String name) throws ClassNotFoundException {

        Class<?> c = loadedClass.get(name);
        if (c == null) {
            synchronized (loadedClass) {
                if (c == null) {
                    c = super.brokenLoadClass(name);
                    loadedClass.put(name, c);
                }
            }
        }

        return c;
    }
}
