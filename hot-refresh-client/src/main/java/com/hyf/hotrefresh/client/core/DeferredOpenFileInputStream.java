package com.hyf.hotrefresh.client.core;

import java.io.*;
import java.nio.file.Files;

/**
 * 延迟打开文件流，减少文件占用时间
 *
 * @author baB_hyf
 * @date 2022/05/19
 */
public class DeferredOpenFileInputStream extends FilterInputStream {

    private File file;
    private volatile InputStream in;

    public DeferredOpenFileInputStream(File file) {
        super(null);
        this.file = file;
    }

    public int read() throws IOException {
        makesureOpen();
        return in.read();
    }

    public int read(byte[] bytes) throws IOException {
        makesureOpen();
        return read(bytes, 0, bytes.length);
    }

    public int read(byte[] bytes, int off, int len) throws IOException {
        makesureOpen();
        return in.read(bytes, off, len);
    }

    public long skip(long n) throws IOException {
        makesureOpen();
        return in.skip(n);
    }

    public int available() throws IOException {
        makesureOpen();
        return in.available();
    }

    public void close() throws IOException {
        makesureOpen();
        in.close();
    }

    public synchronized void mark(int readLimit) {
        try {
            makesureOpen();
            in.mark(readLimit);
        } catch (IOException e) {
            throw new RuntimeException("File open failed", e);
        }
    }

    public synchronized void reset() throws IOException {
        makesureOpen();
        in.reset();
    }

    public boolean markSupported() {
        try {
            makesureOpen();
            return in.markSupported();
        } catch (IOException e) {
            throw new RuntimeException("File open failed", e);
        }
    }

    private void makesureOpen() throws IOException {
        if (in == null) {
            in = Files.newInputStream(file.toPath());
        }
    }
}
