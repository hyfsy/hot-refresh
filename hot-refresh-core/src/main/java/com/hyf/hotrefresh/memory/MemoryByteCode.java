package com.hyf.hotrefresh.memory;

import javax.tools.SimpleJavaFileObject;
import java.io.*;
import java.net.URI;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
class MemoryByteCode extends SimpleJavaFileObject {

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private final String                className;
    private final byte[]                bytes;

    public MemoryByteCode(String className) {
        this(className, null);
    }

    public MemoryByteCode(String className, byte[] bytes) {
        super(URI.create("byte:///" + className.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
        this.className = className;
        this.bytes = bytes;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        if (bytes == null) {
            throw new IllegalStateException("bytes is null");
        }
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        baos.reset();
        return baos;
    }

    public byte[] getByteCode() {
        return baos.toByteArray();
    }

    public String getClassName() {
        return className;
    }
}
