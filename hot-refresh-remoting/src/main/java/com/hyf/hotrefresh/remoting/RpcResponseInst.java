package com.hyf.hotrefresh.remoting;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public enum RpcResponseInst implements EnumCodeAware {

    NONE((byte) 0), //
    CREATE((byte) 1), //
    MODIFY((byte) 2), //
    DELETE((byte) 3), //
    LOG((byte) 4), //
    ;

    private byte code;

    RpcResponseInst(byte code) {
        this.code = code;
    }

    public static RpcResponseInst getInst(byte code) {
        for (RpcResponseInst inst : values()) {
            if (inst.code == code) {
                return inst;
            }
        }

        throw new IllegalArgumentException("Response inst code not support: " + code);
    }

    @Override
    public byte getCode() {
        return code;
    }
}
