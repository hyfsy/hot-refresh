package com.hyf.hotrefresh.classloader;

import java.net.URL;

/**
 * @author baB_hyf
 * @date 2022/05/12
 */
public class DelegateClassLoader extends URLOperateExportClassLoader {

    private ClassLoader delegate;

    public DelegateClassLoader(ClassLoader delegate) {
        super(new URL[] {});
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
