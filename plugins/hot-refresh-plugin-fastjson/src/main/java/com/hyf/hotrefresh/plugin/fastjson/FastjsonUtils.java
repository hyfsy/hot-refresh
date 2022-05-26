package com.hyf.hotrefresh.plugin.fastjson;

import com.hyf.hotrefresh.common.util.ReflectionUtils;
import com.hyf.hotrefresh.core.util.InfraUtils;

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
            Class<?> SerializerFeatureClass = InfraUtils.forName("com.alibaba.fastjson.serializer.SerializerFeature");
            Class<?> JSONClass = InfraUtils.forName("com.alibaba.fastjson.JSON");
            toJSONStringMethod = ReflectionUtils.getMethod(JSONClass, "toJSONString", Object.class, Array.newInstance(SerializerFeatureClass, 1).getClass());
        }

        if (SerializerFeatureArray == null) {
            Class<?> SerializerFeatureClass = InfraUtils.forName("com.alibaba.fastjson.serializer.SerializerFeature");
            Field PrettyFormatField = ReflectionUtils.getField(SerializerFeatureClass, "PrettyFormat");
            Object PrettyFormatFieldObject = ReflectionUtils.invokeField(PrettyFormatField, null);
            SerializerFeatureArray = Array.newInstance(SerializerFeatureClass, 1);
            Array.set(SerializerFeatureArray, 0, PrettyFormatFieldObject);
        }

        return ReflectionUtils.invokeMethod(toJSONStringMethod, null, o, SerializerFeatureArray);
    }
}
