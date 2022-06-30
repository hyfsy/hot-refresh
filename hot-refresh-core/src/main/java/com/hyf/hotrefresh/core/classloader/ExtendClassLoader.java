package com.hyf.hotrefresh.core.classloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

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

        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }

        // Note:  Checking logic in java.lang.invoke.MemberName.checkForTypeAlias
        // relies on the fact that spoofing is impossible if a class has a name
        // of the form "java.*"
        if (!name.startsWith("java.")) {
            return super.loadClass(name, resolve);
        }

        // break

        Class<?> c = null;

        // child load
        try {
            c = this.brokenLoadClass(name);
            if (resolve) {
                resolveClass(c);
            }
        } catch (ClassNotFoundException ignore) {
        }

        // parent load
        if (c == null) {
            c = super.loadClass(name, resolve);
        }

        // all class loader cannot load
        if (c == null) {
            throw new ClassNotFoundException();
        }

        return c;
    }

    @Override
    public URL getResource(String name) {
        URL resource = brokenGetResource(name);
        if (resource != null) {
            return resource;
        }
        return super.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        @SuppressWarnings("unchecked")
        Enumeration<URL>[] tmp = (Enumeration<URL>[]) new Enumeration<?>[2];
        Enumeration<URL> originResource = brokenGetResources(name);
        if (originResource == null) {
            originResource = new EmptyEnumeration<>();
        }
        tmp[0] = originResource;

        if (getParent() != null) {
            tmp[1] = getParent().getResources(name);
        }
        else {
            tmp[1] = new EmptyEnumeration<>();
        }

        return new CompoundEnumeration<>(tmp);
    }

    @Override
    protected Package getPackage(String name) {
        Package pkg = brokenGetPackage(name);
        if (pkg != null) {
            return pkg;
        }
        return super.getPackage(name);
    }

    @Override
    protected Package[] getPackages() {

        Map<String, Package> map = new HashMap<>();

        fillPackages(map, brokenGetPackages());

        if (getParent() != null) {
            fillPackages(map, super.getPackages());
        }
        else {
            // TODO ?
            // fillPackages(map, Package.getSystemPackages());
        }

        return map.values().toArray(new Package[0]);
    }

    protected Class<?> brokenLoadClass(String name) throws ClassNotFoundException {
        return this.findClass(name);
    }

    protected URL brokenGetResource(String name) {
        return this.findResource(name);
    }

    protected Enumeration<URL> brokenGetResources(String name) throws IOException {
        return this.findResources(name);
    }

    protected Package brokenGetPackage(String name) {
        return null;
    }

    protected Package[] brokenGetPackages() {
        return null;
    }

    protected void fillPackages(Map<String, Package> map, Package[] packages) {
        if (packages == null) {
            return;
        }
        for (int i = 0; i < packages.length; i++) {
            String pkgName = packages[i].getName();
            if (map.get(pkgName) == null) {
                map.put(pkgName, packages[i]);
            }
        }
    }

    /**
     * copy from {@link sun.misc.CompoundEnumeration}
     */
    protected static class CompoundEnumeration<E> implements Enumeration<E> {

        private Enumeration<E>[] enums;
        private int              index = 0;

        public CompoundEnumeration(Enumeration<E>[] enums) {
            this.enums = enums;
        }

        private boolean next() {
            while (this.index < this.enums.length) {
                if (this.enums[this.index] != null && this.enums[this.index].hasMoreElements()) {
                    return true;
                }

                ++this.index;
            }

            return false;
        }

        @Override
        public boolean hasMoreElements() {
            return this.next();
        }

        @Override
        public E nextElement() {
            if (!this.next()) {
                throw new NoSuchElementException();
            }
            else {
                return this.enums[this.index].nextElement();
            }
        }
    }

    protected static class EmptyEnumeration<E> implements Enumeration<E> {

        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public E nextElement() {
            return null;
        }
    }
}
