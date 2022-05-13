package com.hyf.hotrefresh.classloader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author baB_hyf
 * @date 2022/05/12
 */
public class CompositeClassLoader extends ExtendClassLoader {

    private final List<ClassLoader> classLoaders = new ArrayList<>();

    public CompositeClassLoader(ClassLoader parent) {
        super(new URL[] {}, parent);
    }

    public void addClassLoader(ClassLoader classLoader) {
        // most expect DelegateClassLoader
        classLoaders.add(classLoader);
    }

    @Override
    protected Class<?> brokenLoadClass(String name) throws ClassNotFoundException {
        for (ClassLoader cl : classLoaders) {
            try {
                return cl.loadClass(name);
            } catch (ClassNotFoundException ignore) {
            }
        }

        throw new ClassNotFoundException();
    }
}
