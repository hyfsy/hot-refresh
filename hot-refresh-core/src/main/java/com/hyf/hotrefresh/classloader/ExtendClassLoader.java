package com.hyf.hotrefresh.classloader;

import java.net.URL;
import java.net.URLStreamHandlerFactory;

/**
 * @author baB_hyf
 * @date 2022/05/12
 */
public class ExtendClassLoader extends URLOperateExportClassLoader {

    public ExtendClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public ExtendClassLoader(URL[] urls) {
        super(urls);
    }

    public ExtendClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        // break

        Class<?> c;
        try {
            c = this.brokenLoadClass(name);
            if (c == null) {
                throw new ClassNotFoundException(name);
            }
        } catch (ClassNotFoundException e) {
            c = super.findClass(name);
        }

        if (resolve) {
            resolveClass(c);
        }

        return c;
    }

    protected Class<?> brokenLoadClass(String name) throws ClassNotFoundException {
        return this.findClass(name);
    }
}
