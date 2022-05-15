package com.hyf.hotrefresh.core.classloader;

import java.net.URL;

/**
 * 仅当前委托类加载器的类加载，无父类加载器的机制
 *
 * @author baB_hyf
 * @date 2022/05/12
 */
public class DelegateClassLoader extends URLOperateExportClassLoader {

    private ClassLoader delegate;

    public DelegateClassLoader(ClassLoader delegate) {
        super(new URL[]{});
        if (delegate == null) {
            throw new IllegalArgumentException("delegate is null");
        }
        this.delegate = delegate;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        if (loadCondition(name)) {
            return delegate.loadClass(name);
        }

        throw new ClassNotFoundException(name);
    }

    protected boolean loadCondition(String name) {
        return true;
    }
}
