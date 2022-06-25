package com.hyf.hotrefresh.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author baB_hyf
 * @date 2022/05/27
 */
public abstract class FastReflectionUtils extends ReflectionUtils {

    public static Optional<Class<?>> forNameNoException(String className) {
        try {
            return Optional.of(Class.forName(className));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public static <T> T fastGetField(Class<?> clazz, String fieldName) {
        return fastGetField(null, clazz, fieldName);
    }

    public static <T> T fastGetField(Object o, Class<?> clazz, String fieldName) {
        Field field = getField(clazz, fieldName);
        return invokeFieldGet(field, o);
    }

    public static <T> Optional<T> fastGetFieldNoException(Class<?> clazz, String fieldName) {
        try {
            return Optional.ofNullable(fastGetField(clazz, fieldName));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static void fastSetField(Class<?> clazz, String fieldName, Object value) {
        fastSetField(null, clazz, fieldName, value);
    }

    public static void fastSetField(Object o, Class<?> clazz, String fieldName, Object value) {
        Field field = getField(clazz, fieldName);
        invokeFieldSet(field, o, value);
    }

    public static Optional<Boolean> fastSetFieldNoException(Object o, Class<?> clazz, String fieldName, Object value) {
        try {
            fastSetField(o, clazz, fieldName, value);
            return Optional.of(true);
        } catch (Exception e) {
            return Optional.of(false);
        }
    }

    public static <T> Optional<T> fastGetFieldNoException(Object o, Class<?> clazz, String fieldName) {
        try {
            return Optional.ofNullable(fastGetField(o, clazz, fieldName));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static <T> T fastInvokeMethod(Class<?> clazz, String methodName) {
        return fastInvokeMethod(null, clazz, methodName);
    }

    public static <T> T fastInvokeMethod(Object o, Class<?> clazz, String methodName) {
        Method method = getMethod(clazz, methodName);
        return invokeMethod(method, o);
    }

    public static <T> T fastInvokeMethod(Class<?> clazz, String methodName, Class<?>[] argTypes, Object... args) {
        return fastInvokeMethod(null, clazz, methodName, argTypes, args);
    }

    public static <T> T fastInvokeMethod(Object o, Class<?> clazz, String methodName, Class<?>[] argTypes, Object... args) {
        Method method = getMethod(clazz, methodName, argTypes);
        return invokeMethod(method, o, args);
    }

    public static <T> Optional<T> fastInvokeMethodNoException(Class<?> clazz, String methodName) {
        try {
            return Optional.ofNullable(fastInvokeMethod(clazz, methodName));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> fastInvokeMethodNoException(Object o, Class<?> clazz, String methodName) {
        try {
            return Optional.ofNullable(fastInvokeMethod(o, clazz, methodName));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> fastInvokeMethodNoException(Class<?> clazz, String methodName, Class<?>[] argTypes, Object... args) {
        try {
            return Optional.ofNullable(fastInvokeMethod(null, clazz, methodName, argTypes, args));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> fastInvokeMethodNoException(Object o, Class<?> clazz, String methodName, Class<?>[] argTypes, Object... args) {
        try {
            return Optional.ofNullable(fastInvokeMethod(o, clazz, methodName, argTypes, args));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
