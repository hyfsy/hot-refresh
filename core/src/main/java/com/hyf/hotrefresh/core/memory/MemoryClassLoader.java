package com.hyf.hotrefresh.core.memory;

import com.hyf.hotrefresh.core.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO 为什么不考虑先加载内存类而要先找父类？因为该类加载器出来的类不是原来的类，导致spring通过这里找到的类找对象找不到的问题
 *
 * @author baB_hyf
 * @date 2021/12/12
 */
public class MemoryClassLoader extends ClassLoader {

    /** full class name -> class bytes */
    private static final Map<String, byte[]> bytesCache = new ConcurrentHashMap<>();

    /** full class name -> refreshed class */
    private static final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

    private static final Object LOCK = new Object();

    static {
        registerAsParallelCapable();
    }

    public MemoryClassLoader(ClassLoader parent) {
        super(parent);
    }

    public static MemoryClassLoader newInstance() {
        return newInstance(Util.getOriginContextClassLoader());
    }

    public static MemoryClassLoader newInstance(ClassLoader parent) {
        return new MemoryClassLoader(parent);
    }

    public void store(String className, byte[] bytes) {
        synchronized (LOCK) {
            bytesCache.put(className, bytes);
            classCache.remove(className);
        }
    }

    public void store(Map<String, byte[]> compiledBytes) {
        synchronized (LOCK) {
            bytesCache.putAll(compiledBytes);
            compiledBytes.forEach((name, bytes) -> classCache.remove(name));
        }
    }

    public byte[] get(String className) {
        return bytesCache.get(className);
    }

    public Map<String, byte[]> getAll() {
        synchronized (LOCK) {
            return new HashMap<>(bytesCache);
        }
    }

    public Class<?> remove(String className) {
        synchronized (LOCK) {
            Class<?> clazz = getClass(className);
            bytesCache.remove(className);
            classCache.remove(className);
            return clazz;
        }
    }

    public Class<?> getClass(String className) {

        // 不破坏双亲委派的情况使用，优先从父类加载器中加载该类
        try {
            return super.loadClass(className);
        } catch (ClassNotFoundException ignored) {
            throw new RuntimeException("Cannot find class: " + className);
        }

        // 破坏双亲委派的情况使用
        // try {
        //     return findClass(className);
        // } catch (ClassNotFoundException e) {
        //     throw new RuntimeException("Cannot find class: " + className);
        // }
    }

    public List<Class<?>> clear() {
        List<Class<?>> classList = new ArrayList<>();

        synchronized (LOCK) {
            bytesCache.keySet().forEach(className -> classList.add(getClass(className)));
            bytesCache.clear();
            classCache.clear();
        }

        return classList;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        synchronized (LOCK) {
            Class<?> clazz = classCache.get(name);
            if (clazz == null) {
                byte[] bytes = bytesCache.get(name);
                if (bytes != null) {
                    clazz = defineClass(name, bytes, 0, bytes.length);
                    classCache.put(name, clazz);
                }
            }
            if (clazz != null) {
                return clazz;
            }
        }

        return super.findClass(name);
    }

}
