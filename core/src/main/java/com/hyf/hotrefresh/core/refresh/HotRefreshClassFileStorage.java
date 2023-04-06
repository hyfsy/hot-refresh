package com.hyf.hotrefresh.core.refresh;

import com.hyf.hotrefresh.common.util.FileUtils;
import com.hyf.hotrefresh.common.util.IOUtils;

import java.io.*;
import java.util.regex.Matcher;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class HotRefreshClassFileStorage implements ClassFileStorage {

    @Override
    public String getStorageHome() {
        return ClassFileStorage.OUTPUT_HOME;
    }

    @Override
    public void write(String className, byte[] bytes) {
        FileUtils.safeWrite(getClassFile(className, true), new ByteArrayInputStream(bytes));
    }

    @Override
    public void delete(String className) {
        FileUtils.delete(getClassFile(className, true));
    }

    @Override
    public void clear() {
        FileUtils.delete(getClassDirectory());
    }

    @Override
    public byte[] get(String className) {
        File classFile = getClassFile(className, true);
        try (InputStream is = new FileInputStream(classFile)) {
            return IOUtils.readAsByteArray(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File getClassFile(String className) {
        return getClassFile(className, false);
    }

    private File getClassDirectory() {
        return FileUtils.getFile(getStorageHome(), false);
    }

    private File getClassFile(String className, boolean create) {
        String storePath = OUTPUT_HOME + File.separator + className.replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + ".class";
        return FileUtils.getFile(storePath, create);
    }
}
