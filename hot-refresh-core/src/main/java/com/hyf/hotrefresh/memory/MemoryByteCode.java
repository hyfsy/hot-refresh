package com.hyf.hotrefresh.memory;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
class MemoryByteCode extends SimpleJavaFileObject {

    private static final char   PKG_SEPARATOR     = '.';
    private static final char   DIR_SEPARATOR     = '/';

    private ByteArrayOutputStream baos;

    public MemoryByteCode(String className) {
        super(URI.create("byte:///" + className.replace(PKG_SEPARATOR, DIR_SEPARATOR)
                + Kind.CLASS.extension), Kind.CLASS);
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        if (baos == null) {
            baos = new ByteArrayOutputStream();
        }
        return baos;
    }

    public byte[] getByteCode() {
        return baos.toByteArray();
    }

    public String getClassName() {
        String className = getName();
        className = className.replace(DIR_SEPARATOR, PKG_SEPARATOR);
        className = className.substring(1, className.indexOf(Kind.CLASS.extension));
        return className;
    }

}
