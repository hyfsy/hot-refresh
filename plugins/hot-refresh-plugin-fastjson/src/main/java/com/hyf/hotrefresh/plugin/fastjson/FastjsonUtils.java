package com.hyf.hotrefresh.plugin.fastjson;

import com.hyf.hotrefresh.common.util.ReflectUtils;
import com.hyf.hotrefresh.core.util.Util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public abstract class FastjsonUtils {

    private static Method toJSONStringMethod;
    private static Object SerializerFeatureArray;

    public static String objectToJson(Object o) {

        if (toJSONStringMethod == null) {
            Class<?> SerializerFeatureClass = Util.getInfrastructureJarClassLoader().forName("com.alibaba.fastjson.serializer.SerializerFeature");
            Class<?> JSONClass = Util.getInfrastructureJarClassLoader().forName("com.alibaba.fastjson.JSON");
            toJSONStringMethod = ReflectUtils.getMethod(JSONClass, "toJSONString", Object.class, Array.newInstance(SerializerFeatureClass, 1).getClass());
        }

        if (SerializerFeatureArray == null) {
            Class<?> SerializerFeatureClass = Util.getInfrastructureJarClassLoader().forName("com.alibaba.fastjson.serializer.SerializerFeature");
            Field PrettyFormatField = ReflectUtils.getField(SerializerFeatureClass, "PrettyFormat");
            Object PrettyFormatFieldObject = ReflectUtils.invokeField(PrettyFormatField, null);
            SerializerFeatureArray = Array.newInstance(SerializerFeatureClass, 1);
            Array.set(SerializerFeatureArray, 0, PrettyFormatFieldObject);
        }

        return ReflectUtils.invokeMethod(toJSONStringMethod, null, o, SerializerFeatureArray);
    }
}