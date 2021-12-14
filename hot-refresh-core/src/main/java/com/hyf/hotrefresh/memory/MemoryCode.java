package com.hyf.hotrefresh.memory;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class MemoryCode extends SimpleJavaFileObject {

    private final String fileName;
    private final String content;

    public MemoryCode(String fileName, String content) {
        super(URI.create("string:///" + fileName.substring(0, fileName.length() - Kind.SOURCE.extension.length()).replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.fileName = fileName;
        this.content = content;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return content;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContent() {
        return content;
    }
}
