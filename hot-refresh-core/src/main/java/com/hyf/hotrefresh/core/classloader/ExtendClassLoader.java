package com.hyf.hotrefresh.core.classloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.util.Enumeration;
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
        @SuppressWarnings("unchecked")
        Enumeration<URL>[] tmp = (Enumeration<URL>[]) new Enumeration<?>[2];
        tmp[0] = this.findResources(name);
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

    /**
     * copy from {@link sun.misc.CompoundEnumeration}
     */
    protected static class CompoundEnumeration<E> implements Enumeration<E> {
        private Enumeration<E>[] enums;
        private int index = 0;

        public CompoundEnumeration(Enumeration<E>[] enums) {
            this.enums = enums;
        }

        private boolean next() {
            while(this.index < this.enums.length) {
                if (this.enums[this.index] != null && this.enums[this.index].hasMoreElements()) {
                    return true;
                }

                ++this.index;
            }

            return false;
        }

        public boolean hasMoreElements() {
            return this.next();
        }

        public E nextElement() {
            if (!this.next()) {
                throw new NoSuchElementException();
            } else {
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
