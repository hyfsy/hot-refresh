package com.hyf.hotrefresh.core.classloader;

import java.net.URL;
import java.net.URLStreamHandlerFactory;

/**
 * 破坏双亲委派的类加载模型
 *
 * @author baB_hyf
 * @date 2022/05/12
 */
public class ExtendClassLoader extends URLOperateExportClassLoader {

    public ExtendClassLoader(ClassLoader parent) {
        this(new URL[]{}, parent);
    }

    public ExtendClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public ExtendClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        // break

        // child load
        Class<?> c = null;
        try {
            c = this.brokenLoadClass(name);
            if (c == null) {
                throw new ClassNotFoundException(name);
            }
        } catch (ClassNotFoundException ignore) {
        }

        // parent load
        if (c == null) {
            c = getParent().loadClass(name);
        }

        // all class loader cannot load
        if (c == null) {
            throw new ClassNotFoundException();
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
