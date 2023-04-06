package com.hyf.hotrefresh.core.refresh;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.Services;
import com.hyf.hotrefresh.common.util.FileUtils;
import com.hyf.hotrefresh.core.memory.MemoryClassLoader;
import com.hyf.hotrefresh.core.util.Util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2023/04/05
 */
public class HotRefreshClassLoader extends MemoryClassLoader {

    private static final ThreadLocal<ClassLoader> cclPerThreadLocal = new ThreadLocal<>();

    private static ClassFileStorage classFileStorage;

    static {
        registerAsParallelCapable();
    }

    static {
        initClassFileStorage();
        addOutputHome();
    }

    public HotRefreshClassLoader(ClassLoader parent) {
        super(parent);
    }

    public static HotRefreshClassLoader newInstance() {
        return newInstance(Util.getOriginContextClassLoader());
    }

    public static HotRefreshClassLoader newInstance(ClassLoader parent) {
        return new HotRefreshClassLoader(parent);
    }

    public static void bind() {
        if (cclPerThreadLocal.get() == null) {
            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(newInstance());
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

    /**
     * TODO 为什么存在这种情况？能否去掉？
     * <p>
     * for scl to load the new compiled class dynamic added by hot refresh
     */
    private static void addOutputHome() {
        ClassLoader scl = ClassLoader.getSystemClassLoader();
        if (scl instanceof URLClassLoader) {
            String storageHome = classFileStorage.getStorageHome();
            if (storageHome == null || "".equals(storageHome.trim())) {
                Log.warn("class file storage home must not be null");
                return;
            }
            File outputHome = FileUtils.getDirectory(storageHome);
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
            classFileStorage = new HotRefreshClassFileStorage();
        }
    }

    @Override
    public void store(String className, byte[] bytes) {
        super.store(className, bytes);
        classFileStorage.write(className, bytes);
    }

    @Override
    public void store(Map<String, byte[]> compiledBytes) {
        super.store(compiledBytes);
        compiledBytes.forEach((name, bytes) -> classFileStorage.write(name, bytes));
    }

    @Override
    public Class<?> remove(String className) {
        Class<?> removedClass = super.remove(className);
        classFileStorage.delete(className);
        return removedClass;
    }

    @Override
    public List<Class<?>> clear() {
        List<Class<?>> clearedClass = super.clear();
        classFileStorage.clear();
        return clearedClass;
    }
}
