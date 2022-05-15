package com.hyf.hotrefresh.remoting;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public enum RpcRequestInst implements EnumCodeAware {

    CREATE((byte) 1), // 新增class
    MODIFY((byte) 2), // 修改class
    DELETE((byte) 3), // 删除class
    ;

    private byte code;

    RpcRequestInst(byte code) {
        this.code = code;
    }

    public static RpcRequestInst getInst(byte code) {
        for (RpcRequestInst inst : values()) {
            if (inst.code == code) {
                return inst;
            }
        }

        throw new IllegalArgumentException("Request inst code not support: " + code);
    }

    @Override
    public byte getCode() {
        return code;
    }
}
