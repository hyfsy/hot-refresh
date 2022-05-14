package com.hyf.hotrefresh.memory;

import com.hyf.hotrefresh.Constants;

import java.io.IOException;
import java.net.URI;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class MemoryCode extends IOAwareJavaFileObject {

    private final String fileName;
    private       String content;

    public MemoryCode(String fileName) {
        this(fileName, null);
    }

    public MemoryCode(String fileName, String content) {
        super(URI.create("string:///" + fileName.substring(0, fileName.length() - Kind.SOURCE.extension.length()).replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.fileName = fileName;
        this.content = content;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        if (content == null) {
            throw new IllegalStateException("content is null");
        }
        return content;
    }

    @Override
    protected byte[] inputStreamSource() {
        return getContent().getBytes(Constants.MESSAGE_ENCODING);
    }

    @Override
    protected void outputStreamClosed(byte[] bytes) {
        this.content = new String(bytes, Constants.MESSAGE_ENCODING);
    }

    public String getFileName() {
        return fileName;
    }

    public String getContent() {
        return content;
    }
}
