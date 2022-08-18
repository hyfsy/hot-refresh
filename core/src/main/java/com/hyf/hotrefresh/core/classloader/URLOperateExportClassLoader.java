package com.hyf.hotrefresh.core.classloader;


import com.hyf.hotrefresh.common.util.FastReflectionUtils;
import com.hyf.hotrefresh.core.util.InfraUtils;
import com.hyf.hotrefresh.core.util.Util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 暴露额外类加载路径的操作功能
 *
 * @author baB_hyf
 * @date 2022/05/12
 */
public class URLOperateExportClassLoader extends URLClassLoader {

    private final Map<String, URL> registeredURLMap = new ConcurrentHashMap<>(4);

    public URLOperateExportClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public URLOperateExportClassLoader(URL[] urls) {
        super(urls);
    }

    public URLOperateExportClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public String addPath(String identity, String path) {
        URL url = Util.getOriginContextClassLoader().getResource(path);
        URL oldUrl = addPath(identity, url);
        return oldUrl == null ? null : oldUrl.toString();
    }

    public URL addPath(String identity, URL url) {
        checkExistWhenFileURL(url);
        URL oldUrl = registeredURLMap.put(identity, url);
        addPathInner(oldUrl, url);
        return oldUrl;
    }

    public URL removePath(String identity) {
        URL url = registeredURLMap.get(identity);
        if (url != null) {
            removePathInner(url);
        }
        return url;
    }

    public Map<String, URL> getRegisteredURLMap() {
        return Collections.unmodifiableMap(registeredURLMap);
    }

    protected void addPathInner(URL oldUrl, URL url) {
        if (oldUrl != null) {
            removePathInner(oldUrl);
        }
        super.addURL(url);
    }

    protected void removePathInner(URL url) {

        synchronized (this) {
            Object ucp = FastReflectionUtils.fastGetField(this, URLClassLoader.class, "ucp");
            Object ucp_path = FastReflectionUtils.fastGetField(ucp, ucp.getClass(), "path");
            Object ucp_urls = FastReflectionUtils.fastGetField(ucp, ucp.getClass(), "urls");
            Object ucp_lmap = FastReflectionUtils.fastGetField(ucp, ucp.getClass(), "lmap");
            Object ucp_loaders = FastReflectionUtils.fastGetField(ucp, ucp.getClass(), "loaders");
            // Object ucp_lookupCacheURLs = FastReflectionUtils.fastGetField(ucp, ucp.getClass(), "lookupCacheURLs");

            FastReflectionUtils.fastInvokeMethod(ucp_path, ArrayList.class, "remove", new Class[]{Object.class}, url);
            FastReflectionUtils.fastInvokeMethod(ucp_urls, Vector.class, "remove", new Class[]{Object.class}, url);
            Object urlNoFragString = FastReflectionUtils.fastInvokeMethod(InfraUtils.forName("sun.net.util.URLUtil"), "urlNoFragString", new Class[]{URL.class}, url);
            Object loader = FastReflectionUtils.fastInvokeMethod(ucp_lmap, HashMap.class, "remove", new Class[]{Object.class}, urlNoFragString);
            if (loader != null) {
                FastReflectionUtils.fastInvokeMethod(ucp_loaders, ArrayList.class, "remove", new Class[]{Object.class}, loader);
            }
        }
    }

    private void checkExistWhenFileURL(URL url) {
        if ("file".equals(url.getProtocol())) {
            if (!new File(url.getFile()).exists()) {
                throw new RuntimeException("url not exists: " + url);
            }
        }
    }
}
