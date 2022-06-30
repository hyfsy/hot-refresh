package com.hyf.hotrefresh.core.classloader;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.util.ResourceUtils;
import com.hyf.hotrefresh.core.agent.ToolsJarProcessor;
import com.hyf.hotrefresh.core.util.ResourcePersistUtils;
import com.hyf.hotrefresh.core.util.Util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class InfrastructureJarClassLoader extends ExtendClassLoader {

    public static final String INFRASTRUCTURE_FILE_RESOURCE_PATH = "infrastructure.properties";

    private static final String BYTE_BUDDY_LOCAL_PATH = "lib/byte-buddy-agent-1.8.17.jar";
    private static final String ASM_LOCAL_PATH        = "lib/asm-5.2.jar";

    private static volatile Map<String, String> DEFAULT_IDENTITY_MAP;

    private static final InfrastructureJarClassLoader INSTANCE = newInstanceByLocal();

    private final Map<String, Class<?>> loadedClass = new ConcurrentHashMap<>();

    private InfrastructureJarClassLoader(URL... urls) {
        // TODO 是否会因为ccl变化而产生问题？
        super(urls, Util.getOriginContextClassLoader());
    }

    public static InfrastructureJarClassLoader getInstance() {
        return INSTANCE;
    }

    private static InfrastructureJarClassLoader newInstanceByLocal() {
        Map<String, URL> urls = getDefaultInfrastructureURLMap();
        InfrastructureJarClassLoader infra = new InfrastructureJarClassLoader();
        urls.forEach(infra::addPath);
        return infra;
    }

    private static Map<String, URL> getDefaultInfrastructureURLMap() {
        Map<String, URL> urlMap = new HashMap<>();
        ClassLoader ccl = Util.getOriginContextClassLoader();

        // byte-buddy
        URL byteBuddyResource = ccl.getResource(BYTE_BUDDY_LOCAL_PATH);
        URL byteBuddyURL = ResourcePersistUtils.getResourceURL(byteBuddyResource);
        urlMap.put("byte-buddy", byteBuddyURL);

        // asm
        URL asmResource = ccl.getResource(ASM_LOCAL_PATH);
        URL asmURL = ResourcePersistUtils.getResourceURL(asmResource);
        urlMap.put("asm", asmURL);

        // tools
        String toolsJarPath = new ToolsJarProcessor().getToolsJarPath();
        if (toolsJarPath != null) {
            try {
                URL javacURL = ResourcePersistUtils.getResourceURL(new File(toolsJarPath).toURI().toURL());
                urlMap.put("tools", javacURL);
            } catch (MalformedURLException e) {
                Log.error("Failed to add javac source url", e);
            }
        }

        // default
        getDefaultIdentityMap().forEach((identity, path) -> {
            File file = new File(path);
            if (file.exists()) {
                try {
                    urlMap.put(identity, file.toURI().toURL());
                } catch (MalformedURLException e) {
                    Log.error("Failed to set default resource", e);
                }
            }
            else {
                URL resource = ccl.getResource(path);
                URL resourceURL = ResourcePersistUtils.getResourceURL(resource);
                urlMap.put(identity, resourceURL);
            }
        });

        return urlMap;
    }

    /**
     * static method may use this map in a early phase, so operate this map please invoke this method
     *
     * @return default identity map
     */
    public static Map<String, String> getDefaultIdentityMap() {
        if (DEFAULT_IDENTITY_MAP == null) {
            DEFAULT_IDENTITY_MAP = ResourceUtils.readPropertiesAsMap(INFRASTRUCTURE_FILE_RESOURCE_PATH, Util.getOriginContextClassLoader());
        }
        return DEFAULT_IDENTITY_MAP;
    }

    public void registerInfrastructureJar(String identity, String location) {

        URL resource = transferToResourceURL(location);
        if (resource == null) {
            throw new RuntimeException("Resource not exists: " + location);
        }
        URL url = ResourcePersistUtils.getResourceURL(resource);
        registerInfrastructureURL(identity, url);
    }

    public void registerInfrastructureDirectory(String identity, String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("Directory not exists: " + path);
        }
        if (!file.isDirectory()) {
            throw new RuntimeException("Only support add directory");
        }
        try {
            registerInfrastructureURL(identity, file.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException("File url illegal: " + file.getAbsolutePath(), e);
        }
    }

    public void registerInfrastructureURL(String identity, URL url) {
        if (getDefaultIdentityMap().containsKey(identity)) {
            if (Log.isDebugMode()) {
                Log.debug("register default infrastructure resource, ignored: " + identity + " -> " + url);
            }
            return;
        }
        addPath(identity, url);
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
    public URL removePath(String identity) {
        if (getDefaultIdentityMap().containsKey(identity)) {
            return null;
        }
        return super.removePath(identity);
    }

    @Override
    protected Class<?> brokenLoadClass(String name) throws ClassNotFoundException {

        Class<?> c = loadedClass.get(name);
        if (c == null) {
            synchronized (loadedClass) {
                c = loadedClass.get(name);
                if (c == null) {
                    c = super.brokenLoadClass(name);
                    loadedClass.put(name, c);
                }
            }
        }

        return c;
    }
}
