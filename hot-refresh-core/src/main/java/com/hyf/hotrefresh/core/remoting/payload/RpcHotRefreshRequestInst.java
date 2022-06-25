package com.hyf.hotrefresh.core.remoting.payload;

import com.hyf.hotrefresh.remoting.rpc.enums.EnumCodeAware;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public enum RpcHotRefreshRequestInst implements EnumCodeAware {

    UNKNOWN((byte) 0), // unset
    CREATE((byte) 1), // 新增class
    MODIFY((byte) 2), // 修改class
    DELETE((byte) 3), // 删除class
    ;

    private byte code;

    RpcHotRefreshRequestInst(byte code) {
        this.code = code;
    }

    public static RpcHotRefreshRequestInst getInst(byte code) {
        for (RpcHotRefreshRequestInst inst : values()) {
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
