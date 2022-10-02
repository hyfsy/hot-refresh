package com.hyf.hotrefresh.core.extend;

import com.hyf.hotrefresh.common.util.FileUtils;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.core.refresh.HotRefresher;
import com.hyf.hotrefresh.core.util.InfraUtils;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.util.Collections;

/**
 * @author baB_hyf
 * @date 2022/01/27
 */
public class ClassBytesDumper {

    public static void dump(Class<?> clazz, String storePath) throws Throwable {
        try (FileOutputStream fos = new FileOutputStream(FileUtils.getFile(storePath))) {
            dump(clazz, fos);
        }
    }

    public static void dump(Class<?> clazz, OutputStream os) throws Throwable {
        byte[] bytes = dump(clazz);
        if (bytes != null && os != null) {
            FileUtils.safeWrite(new ByteArrayInputStream(bytes), os);
        }
    }

    public static byte[] dump(Class<?> clazz) throws Throwable {
        Instrumentation instrumentation = InfraUtils.getInstrumentation();

        ClassBytesDumpTransformer classDumpTransformer = new ClassBytesDumpTransformer(Collections.singleton(clazz), new File("E:\\test\\"));
        instrumentation.addTransformer(classDumpTransformer, true);

        HotRefresher.stop();
        try {
            instrumentation.retransformClasses(clazz);
        } catch (Throwable ignored) {
            // reTransform failed but the bytes has been collected
        } finally {
            instrumentation.removeTransformer(classDumpTransformer);
            HotRefresher.start();
        }

        return classDumpTransformer.getDumpResult().get(clazz);
    }

    public static void dumpNoTransformed(Class<?> clazz, String storePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(FileUtils.getFile(storePath))) {
            dumpNoTransformed(clazz, fos);
        }
    }

    public static void dumpNoTransformed(Class<?> clazz, OutputStream os) throws IOException {
        String classFilePath = clazz.getName().replace('.', '/') + ".class";
        InputStream is = clazz.getClassLoader().getResourceAsStream(classFilePath);
        if (is == null) {
            throw new FileNotFoundException(classFilePath);
        }
        byte[] bytes = IOUtils.readAsByteArray(is);
        dump(bytes, os);
    }

    public static void dump(byte[] bytes, String storePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(FileUtils.getFile(storePath))) {
            dump(bytes, fos);
        }
    }

    public static void dump(byte[] bytes, OutputStream os) {
        FileUtils.safeWrite(new ByteArrayInputStream(bytes), os);
    }
}
