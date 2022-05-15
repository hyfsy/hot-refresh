package com.hyf.hotrefresh.remoting;

import java.util.function.Supplier;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public enum RpcMessageType implements EnumCodeAware {

    REQUEST((byte) 1, RpcRequest::new), //
    BATCH_REQUEST((byte) 2, RpcBatchRequest::new), //
    RESPONSE((byte) 3, RpcResponse::new), //
    ;

    private byte                 code;
    private Supplier<RpcMessage> messageSupplier;

    RpcMessageType(byte code, Supplier<RpcMessage> messageSupplier) {
        this.code = code;
        this.messageSupplier = messageSupplier;
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
        return (T) messageSupplier.get();
    }

    @Override
    public byte getCode() {
        return code;
    }
}
