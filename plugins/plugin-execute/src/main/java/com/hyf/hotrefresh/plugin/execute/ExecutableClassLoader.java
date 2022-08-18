package com.hyf.hotrefresh.plugin.execute;

import com.hyf.hotrefresh.core.memory.MemoryClassLoader;
import com.hyf.hotrefresh.core.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/17
 */
public class ExecutableClassLoader extends MemoryClassLoader {

    private static final List<String> executableClassNameCache = new ArrayList<>();

    public ExecutableClassLoader(ClassLoader parent) {
        super(parent);
    }

    public static ExecutableClassLoader createInstance() {
        return new ExecutableClassLoader(Util.getOriginContextClassLoader());
    }

    public static List<String> getExecutableClassNameCache() {
        return Collections.unmodifiableList(executableClassNameCache);
    }

    @Override
    public void store(String className, byte[] bytes) {
        super.store(className, bytes);
        executableClassNameCache.add(className);
    }

    @Override
    public void store(Map<String, byte[]> compiledBytes) {
        super.store(compiledBytes);
        executableClassNameCache.addAll(compiledBytes.keySet());
    }

    @Override
    public Class<?> remove(String className) {
        Class<?> clazz = super.remove(className);
        executableClassNameCache.remove(className);
        return clazz;
    }

    @Override
    public List<Class<?>> clear() {
        List<Class<?>> classes = super.clear();
        executableClassNameCache.clear();
        return classes;
    }
}
