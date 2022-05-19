package com.hyf.hotrefresh.client.core;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * 延迟打开文件流，减少文件占用时间
 *
 * @author baB_hyf
 * @date 2022/05/19
 */
public class DeferredOpenFileInputStream extends FilterInputStream {

    private          File        file;
    private volatile InputStream in;

    public DeferredOpenFileInputStream(File file) {
        super(null);
        this.file = file;
    }

    @Override
    public int read() throws IOException {
        ensureOpen();
        return in.read();
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        ensureOpen();
        return read(bytes, 0, bytes.length);
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {
        ensureOpen();
        return in.read(bytes, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        ensureOpen();
        return in.skip(n);
    }

    @Override
    public int available() throws IOException {
        ensureOpen();
        return in.available();
    }

    @Override
    public void close() throws IOException {
        ensureOpen();
        in.close();
    }

    @Override
    public synchronized void mark(int readLimit) {
        try {
            ensureOpen();
            in.mark(readLimit);
        } catch (IOException e) {
            throw new RuntimeException("File open failed", e);
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        ensureOpen();
        in.reset();
    }

    @Override
    public boolean markSupported() {
        try {
            ensureOpen();
            return in.markSupported();
        } catch (IOException e) {
            throw new RuntimeException("File open failed", e);
        }
    }

    private void ensureOpen() throws IOException {
        if (in == null) {
            in = Files.newInputStream(file.toPath());
        }
    }
}
