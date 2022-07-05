package com.hyf.hotrefresh.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public abstract class ReflectionUtils {

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to get field: " + clazz + "." + fieldName, e);
        }
    }

    public static <T> T invokeFieldGet(Field field, Object obj) {
        try {
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to invoke field: " + field.getName(), e);
        }
    }

    public static void invokeFieldSet(Field field, Object obj, Object value) {
        try {
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to invoke field: " + field.getName(), e);
        }
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... args) {
        try {
            return clazz.getDeclaredMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to get method: " + clazz + "." + methodName, e);
        }
    }

    public static <T> T invokeMethod(Method method, Object obj, Object... args) {
        try {
            method.setAccessible(true);
            return (T) method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to invoke method: " + method.getName(), e);
        }
    }

    public static <T> T newClassInstance(Class<T> clazz) {
        return newClassInstance(clazz, new Class[]{});
    }

    public static <T> T newClassInstance(Class<T> clazz, Class<?>[] argsTypes, Object... args) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(argsTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to new instance of " + clazz.getName() + " with args " + Arrays.stream(argsTypes).map(Class::getName).collect(Collectors.joining(",")), e);
        }
    }

    public static Class<?> forName(String className, ClassLoader classLoader) {
        try {
            return Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to get class: " + className, e);
        }
    }
}
