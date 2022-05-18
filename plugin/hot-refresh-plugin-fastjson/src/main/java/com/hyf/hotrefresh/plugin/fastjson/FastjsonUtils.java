package com.hyf.hotrefresh.plugin.fastjson;

import com.hyf.hotrefresh.common.util.ReflectUtils;
import com.hyf.hotrefresh.core.util.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public abstract class FastjsonUtils {

    private static Method toJSONStringMethod;
    private static Object PrettyFormatFieldObject;

    public static String objectToJson(Object o) {

        if (toJSONStringMethod == null) {
            Class<?> JSONClass = Util.getInfrastructureJarClassLoader().forName("com.alibaba.fastjson.JSON");
            toJSONStringMethod = ReflectUtils.getMethod(JSONClass, "toJSONString");
        }

        if (PrettyFormatFieldObject == null) {
            Class<?> SerializerFeatureClass = Util.getInfrastructureJarClassLoader().forName("com.alibaba.fastjson.serializer.SerializerFeature");
            Field PrettyFormatField = ReflectUtils.getField(SerializerFeatureClass, "PrettyFormat");
            PrettyFormatFieldObject = ReflectUtils.invokeField(PrettyFormatField, null);
        }

        return ReflectUtils.invokeMethod(toJSONStringMethod, null, o, PrettyFormatFieldObject);
    }
}
