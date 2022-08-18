package com.hyf.hotrefresh.remoting.rpc.enums;

import java.io.*;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public enum RpcMessageCodec implements EnumCodeAware {

    JDK((byte) 1, new JdkCodec()), //
    ;

    private byte  code;
    private Codec codec;

    RpcMessageCodec(byte code, Codec codec) {
        this.code = code;
        this.codec = codec;
    }

    public static RpcMessageCodec getCodec(byte code) {
        for (RpcMessageCodec codec : values()) {
            if (codec.code == code) {
                return codec;
            }
        }

        throw new IllegalArgumentException("Message codec code not support: " + code);
    }

    @Override
    public byte getCode() {
        return code;
    }

    public <T> byte[] encode(T t) {
        return this.codec.encode(t);
    }

    public <T> T decode(byte[] bytes) {
        return this.codec.decode(bytes);
    }

    public interface Codec {
        <T> byte[] encode(T t);

        <T> T decode(byte[] bytes);
    }

    public static class JdkCodec implements Codec {

        @Override
        public <T> byte[] encode(T t) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(t);
                return baos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("Jdk codec encode failed", e);
            }
        }

        @Override
        public <T> T decode(byte[] bytes) {
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                return (T) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Jdk codec decode failed", e);
            }
        }
    }
}
