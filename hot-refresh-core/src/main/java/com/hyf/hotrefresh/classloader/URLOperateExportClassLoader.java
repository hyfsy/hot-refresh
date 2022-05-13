package com.hyf.hotrefresh.classloader;

import com.hyf.hotrefresh.util.ResourceUtil;
import com.hyf.hotrefresh.util.Util;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * 暴露额外类加载路径的添加功能
 *
 * @author baB_hyf
 * @date 2022/05/12
 */
public class URLOperateExportClassLoader extends URLClassLoader {

    public URLOperateExportClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public URLOperateExportClassLoader(URL[] urls) {
        super(urls);
    }

    public URLOperateExportClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public void addPath(String path) {
        URL resource = Util.getOriginContextClassLoader().getResource(path);
        URL url = ResourceUtil.getResourceURL(resource);
        addURL(url);
    }
}
