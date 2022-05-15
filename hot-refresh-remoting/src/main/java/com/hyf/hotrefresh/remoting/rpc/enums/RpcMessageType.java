package com.hyf.hotrefresh.remoting.rpc.enums;

import com.hyf.hotrefresh.remoting.rpc.RpcMessage;
import com.hyf.hotrefresh.remoting.rpc.handler.*;

import java.util.function.Supplier;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public enum RpcMessageType implements EnumCodeAware {

    REQUEST((byte) 1, RpcRequestHandler::new), //
    RESPONSE((byte) 2, RpcResponseHandler::new), //
    BATCH_REQUEST((byte) 3, RpcBatchRequestHandler::new), //
    BATCH_RESPONSE((byte) 4, RpcBatchResponseHandler::new), //
    HEARTBEAT_REQUEST((byte) 5, RpcHeartbeatRequestHandler::new), //
    HEARTBEAT_RESPONSE((byte) 6, RpcHeartbeatResponseHandler::new), //
    ERROR_RESPONSE((byte) 7, RpcErrorResponseHandler::new), //
    SUCCESS_RESPONSE((byte) 8, RpcResponseHandler::new), //
    ;

    private final    Supplier<RpcMessageHandler<? extends RpcMessage, ? extends RpcMessage>> rpcMessageHandlerSupplier;
    private final    byte                                                                    code;
    private volatile RpcMessageHandler<? extends RpcMessage, ? extends RpcMessage>           rpcMessageHandler;

    RpcMessageType(byte code, Supplier<RpcMessageHandler<?, ?>> rpcMessageHandlerSupplier) {
        this.code = code;
        this.rpcMessageHandlerSupplier = rpcMessageHandlerSupplier;
    }

    public static RpcMessageType getMessageType(byte code) {
        for (RpcMessageType type : values()) {
            if (type.code == code) {
                return type;
            }
        }

        throw new IllegalArgumentException("Message type code not support: " + code);
    }

    public <T extends RpcMessage> T createMessage() {
        return (T) rpcMessageHandler.createEmptyRpcMessage();
    }

    @Override
    public byte getCode() {
        return code;
    }

    // lazy init to avoid cyclic dependencies during initialization
    public synchronized RpcMessageHandler<? extends RpcMessage, ? extends RpcMessage> getRpcMessageHandler() {
        if (rpcMessageHandler == null) {
            rpcMessageHandler = rpcMessageHandlerSupplier.get();
        }
        return rpcMessageHandler;
    }
}
