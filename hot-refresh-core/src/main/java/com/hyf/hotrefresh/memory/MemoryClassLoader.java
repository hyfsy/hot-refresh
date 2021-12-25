package com.hyf.hotrefresh.memory;

import com.hyf.hotrefresh.Constants;
import com.hyf.hotrefresh.Util;
import com.hyf.hotrefresh.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class MemoryClassLoader extends ClassLoader {

    private static final String OUTPUT_HOME = Constants.REFRESH_HOME + File.separator + "output";

    /** full class name -> class bytes */
    private static final Map<String, byte[]> bytesCache = new ConcurrentHashMap<>();

    /** full class name -> refreshed class */
    private static final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

    private static final ThreadLocal<ClassLoader> cclPerThreadLocal = new ThreadLocal<>();

    private static final Object LOCK = new Object();

    static {
        registerAsParallelCapable();
    }

    static {
        addOutputHome();
    }

    // for scl to load compiled class
    private static void addOutputHome() {
        ClassLoader scl = ClassLoader.getSystemClassLoader();
        if (scl instanceof URLClassLoader) {
            File outputHome = FileUtil.getFile(OUTPUT_HOME);
            if (outputHome.exists()) {
                FileUtil.delete(outputHome);
            }
            try {
                URL outputURL = outputHome.toURI().toURL();
                if (!Arrays.asList(((URLClassLoader) scl).getURLs()).contains(outputURL)) {
                    Method addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                    addURLMethod.setAccessible(true);
                    addURLMethod.invoke(scl, outputURL);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Failed to invoke method URLClassLoader.addURL", e);
            } catch (MalformedURLException e) {
                throw new RuntimeException("File url illegal: " + outputHome.getAbsolutePath(), e);
            }
        }
    }

    private MemoryClassLoader(ClassLoader parent) {
        super(parent);
    }

    public static MemoryClassLoader newInstance() {
        return new MemoryClassLoader(Util.getOriginContextClassLoader());
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

    public void store(Map<String, byte[]> compiledBytes) {
        synchronized (LOCK) {
            bytesCache.putAll(compiledBytes);
            compiledBytes.forEach((name, bytes) -> classCache.remove(name));
        }

        compiledBytes.forEach((name, bytes) -> {
            String storePath = OUTPUT_HOME + File.separator + name.replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + ".class";
            FileUtil.safeWrite(FileUtil.getFile(storePath), new ByteArrayInputStream(bytes));
        });
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
