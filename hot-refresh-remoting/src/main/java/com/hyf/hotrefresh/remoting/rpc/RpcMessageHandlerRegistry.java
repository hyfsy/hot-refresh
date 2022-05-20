package com.hyf.hotrefresh.remoting.rpc;

import com.hyf.hotrefresh.common.Services;
import com.hyf.hotrefresh.remoting.rpc.enums.HandleSide;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author baB_hyf
 * @date 2022/05/17
 */
public class RpcMessageHandlerRegistry {

    private static final RpcMessageHandlerRegistry INSTANCE = new RpcMessageHandlerRegistry();

    static {
        INSTANCE.initDefaultHandler();
    }

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    /** message type -> handler */
    private Map<Byte, RpcMessageHandler<?, ?>> handlers = new HashMap<>();

    private Map<HandleSide, Map<Byte, RpcMessageHandler<?, ?>>> sideRpcMessageHandlerMap = new HashMap<>();

    public static RpcMessageHandlerRegistry getInstance() {
        return INSTANCE;
    }

    public void register(RpcMessageHandlerRegister register) {
        register.register(this);
    }

    public void register(RpcMessageHandlerRegistrationInfo registrationInfo) {
        byte messageCode = registrationInfo.handleMessageCode();
        HandleSide handleSide = registrationInfo.handleSide();
        RpcMessageHandler<? extends RpcMessage, ? extends RpcMessage> rpcMessageHandler = registrationInfo.rpcMessageHandler();

        RpcMessageHandler<?, ?> oldHandler = handlers.put(messageCode, rpcMessageHandler);
        if (oldHandler != null) {
            throw new RuntimeException("Message code repeat for " + oldHandler.getClass().getName() + " and " + rpcMessageHandler.getClass().getName());
        }

        sideRpcMessageHandlerMap.putIfAbsent(handleSide, new ConcurrentHashMap<>());
        sideRpcMessageHandlerMap.get(handleSide).put(messageCode, rpcMessageHandler);
        if (handleSide == HandleSide.BOTH) {
            sideRpcMessageHandlerMap.get(HandleSide.CLIENT).put(messageCode, rpcMessageHandler);
            sideRpcMessageHandlerMap.get(HandleSide.SERVER).put(messageCode, rpcMessageHandler);
        }
    }

    public RpcMessageHandler<? extends RpcMessage, ? extends RpcMessage> getHandler(byte messageCode) {
        return handlers.get(messageCode);
    }

    public Map<Byte, RpcMessageHandler<?, ?>> getHandlers() {
        return Collections.unmodifiableMap(handlers);
    }

    public Map<Byte, RpcMessageHandler<?, ?>> getHandlersByHandleSide(HandleSide handleSide) {
        return Collections.unmodifiableMap(sideRpcMessageHandlerMap.get(handleSide));
    }

    public void initDefaultHandler() {
        if (initialized.compareAndSet(false, true)) {

            List<RpcMessageHandlerRegister> registers = Services.gets(RpcMessageHandlerRegister.class);
            for (RpcMessageHandlerRegister register : registers) {
                register(register);
            }
            initHandlers();
        }
    }

    private void initHandlers() {
        for (RpcMessageHandler<?, ?> handler : handlers.values()) {
            handler.init(this);
        }
    }
}
