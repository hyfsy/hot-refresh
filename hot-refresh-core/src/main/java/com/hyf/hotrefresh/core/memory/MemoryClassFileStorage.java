package com.hyf.hotrefresh.core.memory;

import com.hyf.hotrefresh.common.util.FileUtils;
import com.hyf.hotrefresh.common.util.IOUtils;

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
        FileUtils.safeWrite(getClassFile(className), new ByteArrayInputStream(bytes));
    }

    @Override
    public void delete(String className) {
        FileUtils.delete(getClassFile(className));
    }

    @Override
    public void clear() {
        FileUtils.delete(FileUtils.getFile(OUTPUT_HOME));
    }

    @Override
    public byte[] get(String className) {
        File classFile = getClassFile(className);
        try (InputStream is = new FileInputStream(classFile)) {
            return IOUtils.readAsByteArray(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File getClassFile(String className) {
        String storePath = OUTPUT_HOME + File.separator + className.replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + ".class";
        return FileUtils.getFile(storePath);
    }
}
