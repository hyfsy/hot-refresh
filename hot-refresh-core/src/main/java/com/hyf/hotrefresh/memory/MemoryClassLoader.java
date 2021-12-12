package com.hyf.hotrefresh.memory;

import com.hyf.hotrefresh.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class MemoryClassLoader extends ClassLoader {

    /** full class name -> class bytes */
    private static final Map<String, byte[]> bytesCache = new ConcurrentHashMap<>();

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

    private static String getClassName(byte[] bytes) {
        return Util.getInfrastructureJarClassLoader().getClassName(bytes);
    }

    public String store(String fileName, byte[] bytes) {
        String className = getClassName(bytes);
        synchronized (LOCK) {
            bytesCache.put(className, bytes);
        }
        return className;
    }

    public byte[] get(String className) {
        synchronized (LOCK) {
            return bytesCache.get(className);
        }
    }

    public byte[] put(String className, byte[] data) {
        synchronized (LOCK) {
            return bytesCache.put(className, data);
        }
    }

    public byte[] remove(String className) {
        synchronized (LOCK) {
            return bytesCache.remove(className);
        }
    }

    public List<Class<?>> clear() {
        List<Class<?>> classList = new ArrayList<>();

        synchronized (LOCK) {
            Set<String> classNameList = bytesCache.keySet();

            classNameList.forEach(className -> {
                try {
                    classList.add(loadClass(className));
                } catch (ClassNotFoundException ignored) {
                }
            });

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
