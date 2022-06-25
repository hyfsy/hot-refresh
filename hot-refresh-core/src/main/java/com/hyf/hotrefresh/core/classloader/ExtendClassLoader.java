package com.hyf.hotrefresh.core.classloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.util.Enumeration;

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

        // Note:  Checking logic in java.lang.invoke.MemberName.checkForTypeAlias
        // relies on the fact that spoofing is impossible if a class has a name
        // of the form "java.*"
        if (!name.startsWith("java.")) {
            try {
                c = this.brokenLoadClass(name);
                if (c == null) {
                    throw new ClassNotFoundException(name);
                }
            } catch (ClassNotFoundException ignore) {
            }
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

    @Override
    public URL getResource(String name) {
        URL resource = this.findResource(name);
        if (resource != null) {
            return resource;
        }
        return super.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL> resources = this.findResources(name);
        if (resources.hasMoreElements()) {
            return resources;
        }
        return super.getResources(name);
    }

    @Override
    protected Package getPackage(String name) {
        // TODO
        return super.getPackage(name);
    }

    @Override
    protected Package[] getPackages() {
        // TODO
        return super.getPackages();
    }

    protected Class<?> brokenLoadClass(String name) throws ClassNotFoundException {
        return this.findClass(name);
    }
}
