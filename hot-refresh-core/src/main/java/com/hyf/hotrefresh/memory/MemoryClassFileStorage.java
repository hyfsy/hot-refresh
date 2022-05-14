package com.hyf.hotrefresh.memory;

import com.hyf.hotrefresh.util.FileUtil;
import com.hyf.hotrefresh.util.IOUtil;

import java.io.*;
import java.util.regex.Matcher;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class MemoryClassFileStorage implements ClassFileStorage {

    @Override
    public String getStorageHome() {
        return ClassFileStorage.OUTPUT_HOME;
    }

    @Override
    public void write(String className, byte[] bytes) {
        FileUtil.safeWrite(getClassFile(className), new ByteArrayInputStream(bytes));
    }

    @Override
    public void delete(String className) {
        FileUtil.delete(getClassFile(className));
    }

    @Override
    public void clear() {
        FileUtil.delete(FileUtil.getFile(OUTPUT_HOME));
    }

    @Override
    public byte[] get(String className) {
        File classFile = getClassFile(className);
        try (InputStream is = new FileInputStream(classFile);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtil.writeTo(is, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File getClassFile(String className) {
        String storePath = OUTPUT_HOME + File.separator + className.replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + ".class";
        return FileUtil.getFile(storePath);
    }
}
