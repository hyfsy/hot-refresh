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

        // 优先从父类加载器加载的原因：
        // remove/clear时得给app加载的类才能refresh成功，否则mem加载的类不会导致任何app类被refresh

        // 不破坏双亲委派的情况使用，优先从父类加载器中加载该类
        try {
            return super.loadClass(className);
        } catch (ClassNotFoundException ignored) {
            throw new RuntimeException("Cannot find class: " + className);
        }

        // 破坏双亲委派的情况使用
        // return brokeGetClass(className);
    }

    private Class<?> brokeGetClass(String className) {
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

        // 优先从父类加载器加载的原因：
        // 子类加载器会造成覆盖问题，应用中存在该类，但被子类加载，类加载器不同，内部类的情况无法reTransform且使用该类时会造成非法访问的权限问题
        // TODO 但优先从父类加载器中加载也会导致问题，storage的问题，导致app一直能加载出来
        //  而关联的业务类找不到的情况，导致reTransform时出现java.lang.InternalError的错误
        //  这问题先不考虑，只有内部类的情况会有问题

        // 不破坏双亲委派的情况使用，优先从父类加载器中加载该类
        try {
            return super.findClass(name);
        } catch (ClassNotFoundException ignored) {
        }

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

        throw new ClassNotFoundException(name);

        // 破坏双亲委派的情况使用
        // return brokeFindClass(name);
    }

    protected Class<?> brokeFindClass(String name) throws ClassNotFoundException {
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
