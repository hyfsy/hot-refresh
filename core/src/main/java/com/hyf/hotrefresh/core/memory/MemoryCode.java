package com.hyf.hotrefresh.core.memory;

import com.hyf.hotrefresh.common.Constants;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class MemoryCode extends IOAwareJavaFileObject {

    private static final Pattern PACKAGE_PATTERN = Pattern.compile("package (.*?);");

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryCode that = (MemoryCode) o;
        return Objects.equals(fileName, that.fileName) && Objects.equals(extractContentIdentity(), that.extractContentIdentity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, extractContentIdentity());
    }

    private String extractContentIdentity() {
        Matcher matcher = PACKAGE_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return content;
    }
}
