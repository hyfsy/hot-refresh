package com.hyf.hotrefresh.core.memory;

import java.net.URI;
import java.util.Arrays;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryByteCode code = (MemoryByteCode) o;
        return Objects.equals(className, code.className) && Arrays.equals(bytes, code.bytes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(className);
        result = 31 * result + Arrays.hashCode(bytes);
        return result;
    }
}
