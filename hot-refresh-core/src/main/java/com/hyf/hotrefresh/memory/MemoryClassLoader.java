package com.hyf.hotrefresh.memory;

import com.hyf.hotrefresh.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class MemoryClassLoader extends ClassLoader {

    /** full class name -> class bytes */
    private static final Map<String, byte[]> bytesCache = new ConcurrentHashMap<>();

    // TODO 缓存加载未重刷新的类
    /** full class name -> refreshed class */
    private static final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

    private static final ThreadLocal<ClassLoader> cclPerThread = new ThreadLocal<>();

    private static final Object LOCK = new Object();

    static {
        registerAsParallelCapable();
    }

    private MemoryClassLoader(ClassLoader parent) {
        super(parent);
    }

    public static MemoryClassLoader newInstance() {
        return new MemoryClassLoader(Util.getOriginContextClassLoader());
    }

    public static void bind() {
        if (cclPerThread.get() == null) {
            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(Util.getThrowawayMemoryClassLoader());
            cclPerThread.set(ccl);
        }
    }

    public static void unBind() {
        ClassLoader ccl = cclPerThread.get();
        Thread.currentThread().setContextClassLoader(ccl);
        cclPerThread.remove();
    }

    public void store(Map<String, byte[]> compiledBytes) {
        synchronized (LOCK) {
            bytesCache.putAll(compiledBytes);
        }
    }

    public byte[] get(String className) {
        synchronized (LOCK) {
            return bytesCache.get(className);
        }
    }

    public Class<?> remove(String className) {
        synchronized (LOCK) {
            Class<?> clazz = getClass(className);
            bytesCache.remove(className);
            return clazz;
        }
    }

    public Class<?> getClass(String className) {
        try {
            return findClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find class: " + className);
        }
    }

    public List<Class<?>> clear() {
        List<Class<?>> classList = new ArrayList<>();

        synchronized (LOCK) {
            bytesCache.keySet().forEach(className -> classList.add(getClass(className)));
            bytesCache.clear();
        }

        return classList;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        synchronized (LOCK) {
            byte[] bytes = bytesCache.get(name);
            if (bytes != null) {
                return defineClass(name, bytes, 0, bytes.length);
            }
        }

        return super.findClass(name);
    }
}
