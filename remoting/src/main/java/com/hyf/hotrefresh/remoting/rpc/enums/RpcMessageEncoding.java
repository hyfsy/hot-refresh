package com.hyf.hotrefresh.remoting.rpc.enums;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public enum RpcMessageEncoding implements EnumCodeAware {

    UTF_8((byte) 1, StandardCharsets.UTF_8), //
    ISO_8859_1((byte) 2, StandardCharsets.ISO_8859_1), //
    ;

    private byte    code;
    private Charset charset;

    RpcMessageEncoding(byte code, Charset charset) {
        this.code = code;
        this.charset = charset;
    }

    public static RpcMessageEncoding getEncoding(byte code) {
        for (RpcMessageEncoding encoding : values()) {
            if (encoding.code == code) {
                return encoding;
            }
        }

        throw new IllegalArgumentException("Message encoding code not support: " + code);
    }

    public static RpcMessageEncoding getEncoding(Charset charset) {
        for (RpcMessageEncoding encoding : values()) {
            if (encoding.charset == charset) {
                return encoding;
            }
        }

        throw new IllegalArgumentException("Message encoding not support: " + charset.name());
    }

    @Override
    public byte getCode() {
        return code;
    }

    public Charset getCharset() {
        return charset;
    }
}
