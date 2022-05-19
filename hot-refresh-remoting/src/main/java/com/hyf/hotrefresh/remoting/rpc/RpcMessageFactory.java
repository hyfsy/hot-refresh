package com.hyf.hotrefresh.remoting.rpc;

import com.hyf.hotrefresh.common.util.ReflectUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class RpcMessageFactory {

    private static final Map<Class<? extends RpcMessageHandler<?, ?>>, Class<? extends RpcMessage>> cache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends RpcMessage> T createRpcMessage(byte messageTypeCode) {
        RpcMessageHandler<?, ?> handler = RpcMessageHandlerRegistry.getInstance().getHandler(messageTypeCode);

        Class<T> clazz = getRpcMessageClassType((Class<? extends RpcMessageHandler<?, ?>>) handler.getClass());
        return ReflectUtils.newClassInstance(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T extends RpcMessage> Class<T> getRpcMessageClassType(
            Class<? extends RpcMessageHandler<?, ?>> clazz) {
        Class<T> messageClass = (Class<T>) cache.get(clazz);
        if (messageClass != null) {
            return messageClass;
        }

        List<ParameterizedType> allGenericTypes = getAllGenericTypes(clazz);
        for (ParameterizedType parameterizedType : allGenericTypes) {
            Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
            if (actualTypeArgument instanceof Class) { // 泛型是个Class，表示固定的
                Class<T> requestType = (Class<T>) actualTypeArgument;
                boolean messageTypeValid =
                        RpcMessage.class.isAssignableFrom(requestType) && requestType != RpcMessage.class;
                Type rawType = parameterizedType.getRawType();
                boolean handlerTypeValid =
                        rawType instanceof Class<?> && RpcMessageHandler.class.isAssignableFrom((Class<?>) rawType);
                if (messageTypeValid && handlerTypeValid) {
                    return requestType;
                }
            }
        }

        throw new RuntimeException("Handler class signature is illegal: " + clazz.getName());
    }

    private static List<ParameterizedType> getAllGenericTypes(Type type) {

        List<ParameterizedType> types = new ArrayList<>();

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            types.add(parameterizedType);
            types.addAll(getAllGenericTypes(rawType));
            return types;
        }
        else if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
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
