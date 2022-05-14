package com.hyf.hotrefresh.memory;

import java.net.URI;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
class MemoryByteCode extends IOAwareJavaFileObject {

    private final String className;
    private       byte[] bytes;

    public MemoryByteCode(String className) {
        this(className, null);
    }

    public MemoryByteCode(String className, byte[] bytes) {
        super(URI.create("byte:///" + className.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
        this.className = className;
        this.bytes = bytes;
    }

    @Override
    protected byte[] inputStreamSource() {
        return getByteCode();
    }

    @Override
    protected void outputStreamClosed(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getByteCode() {
        return bytes;
    }

    public String getClassName() {
        return className;
    }
}
