package com.hyf.hotrefresh.remoting.rpc;

import com.hyf.hotrefresh.common.util.ReflectUtils;
import com.hyf.hotrefresh.common.util.TypeUtils;
import com.hyf.hotrefresh.remoting.exception.RpcException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

        if (handler == null) {
            throw new RpcException("Unknown message type code: " + messageTypeCode);
        }

        Class<T> clazz = getRpcMessageClassType((Class<? extends RpcMessageHandler<?, ?>>) handler.getClass());
        return ReflectUtils.newClassInstance(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T extends RpcMessage> Class<T> getRpcMessageClassType(
            Class<? extends RpcMessageHandler<?, ?>> clazz) {
        Class<T> messageClass = (Class<T>) cache.compute(clazz, (h, m) -> {

            if (m == null) {
                List<ParameterizedType> allGenericTypes = TypeUtils.getAllGenericTypes(clazz);
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
            }

            if (m == null) {
                throw new RuntimeException("Handler class signature is illegal: " + clazz.getName());
            }

            return m;
        });

        return messageClass;
    }
}
