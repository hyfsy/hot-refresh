package com.hyf.hotrefresh.memory;

import javax.tools.SimpleJavaFileObject;
import java.io.*;
import java.net.URI;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
abstract class IOAwareJavaFileObject extends SimpleJavaFileObject {

    protected final ByteArrayOutputStream baos = new AutoSyncByteArrayOutputStream();

    protected IOAwareJavaFileObject(URI uri, Kind kind) {
        super(uri, kind);
    }

    @Override
    public InputStream openInputStream() throws IOException {
        if (inputStreamSource() == null) {
            throw new IllegalStateException("inputStreamSource is null");
        }
        return new ByteArrayInputStream(inputStreamSource());
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        baos.reset();
        return baos;
    }

    protected abstract byte[] inputStreamSource();

    protected abstract void outputStreamClosed(byte[] bytes);

    private class AutoSyncByteArrayOutputStream extends ByteArrayOutputStream {

        @Override
        public void close() throws IOException {
            super.close();
            IOAwareJavaFileObject.this.outputStreamClosed(this.toByteArray());
        }
    }
}
