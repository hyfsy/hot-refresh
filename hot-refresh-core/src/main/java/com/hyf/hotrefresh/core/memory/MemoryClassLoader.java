package com.hyf.hotrefresh.core.memory;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.Services;
import com.hyf.hotrefresh.common.util.FileUtils;
import com.hyf.hotrefresh.core.util.Util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class MemoryClassLoader extends ClassLoader {

    /** full class name -> class bytes */
    private static final Map<String, byte[]> bytesCache = new ConcurrentHashMap<>();

    /** full class name -> refreshed class */
    private static final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

    private static final ThreadLocal<ClassLoader> cclPerThreadLocal = new ThreadLocal<>();
    private static final Object                   LOCK              = new Object();
    private static       ClassFileStorage         classFileStorage;

    static {
        registerAsParallelCapable();
    }

    static {
        initClassFileStorage();
        addOutputHome();
    }

    protected MemoryClassLoader(ClassLoader parent) {
        super(parent);
    }

    // for scl to load compiled class
    private static void addOutputHome() {
        ClassLoader scl = ClassLoader.getSystemClassLoader();
        if (scl instanceof URLClassLoader) {
            String storageHome = classFileStorage.getStorageHome();
            if (storageHome == null || "".equals(storageHome.trim())) {
                Log.warn("class file storage home must not be null");
                return;
            }
            File outputHome = FileUtils.getFile(storageHome);
            try {
                URL outputURL = outputHome.toURI().toURL();
                if (!Arrays.asList(((URLClassLoader) scl).getURLs()).contains(outputURL)) {
                    Method addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                    addURLMethod.setAccessible(true);
                    addURLMethod.invoke(scl, outputURL);
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException("File url illegal: " + outputHome.getAbsolutePath(), e);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Failed to invoke method URLClassLoader.addURL", e);
            }
        }
    }

    private static void initClassFileStorage() {
        List<ClassFileStorage> classFileStorages = Services.gets(ClassFileStorage.class);
        if (classFileStorages.iterator().hasNext()) {
            classFileStorage = classFileStorages.iterator().next();
        }
        else {
            classFileStorage = new MemoryClassFileStorage();
        }
    }

    public static MemoryClassLoader newInstance() {
        return new MemoryClassLoader(Util.getOriginContextClassLoader());
    }

    public static MemoryClassLoader newInstance(ClassLoader parent) {
        return new MemoryClassLoader(parent);
    }

    public static void bind() {
        if (cclPerThreadLocal.get() == null) {
            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(Util.getThrowawayMemoryClassLoader());
            cclPerThreadLocal.set(ccl);
        }
    }

    public static void unBind() {
        if (cclPerThreadLocal.get() != null) {
            ClassLoader ccl = cclPerThreadLocal.get();
            Thread.currentThread().setContextClassLoader(ccl);
            cclPerThreadLocal.remove();
        }
    }

    public void store(String className, byte[] bytes) {
        synchronized (LOCK) {
            bytesCache.put(className, bytes);
            classCache.remove(className);
            classFileStorage.write(className, bytes);
        }
    }

    public void store(Map<String, byte[]> compiledBytes) {
        synchronized (LOCK) {
            bytesCache.putAll(compiledBytes);
            compiledBytes.forEach((name, bytes) -> {
                classCache.remove(name);
                classFileStorage.write(name, bytes);
            });
        }
    }

    public byte[] get(String className) {
        synchronized (LOCK) {
            return bytesCache.get(className);
        }
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
            classFileStorage.delete(className);
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
            classCache.clear();
            classFileStorage.clear();
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
