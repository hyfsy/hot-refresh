package com.hyf.hotrefresh.common.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author baB_hyf
 * @date 2022/05/19
 */
public abstract class TypeUtils {

    public static List<ParameterizedType> getAllGenericTypes(Type type) {

        List<ParameterizedType> types = new ArrayList<>();

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Type rawType = parameterizedType.getRawType();
            types.add(parameterizedType);
            types.addAll(getAllGenericTypes(rawType));
            return types;
        } else if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>)type;
            List<Type> genericInterfaces = new ArrayList<>(Arrays.asList(clazz.getGenericInterfaces()));
            Type genericSuperclass = clazz.getGenericSuperclass();
            if (genericSuperclass != null) {
                genericInterfaces.add(genericSuperclass);
            }
            genericInterfaces.forEach(t -> types.addAll(getAllGenericTypes(t)));
        }

        return types;
    }
}
