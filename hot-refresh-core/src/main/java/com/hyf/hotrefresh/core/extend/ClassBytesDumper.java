package com.hyf.hotrefresh.core.extend;

import com.hyf.hotrefresh.common.util.FileUtils;
import com.hyf.hotrefresh.core.refresh.HotRefresher;
import com.hyf.hotrefresh.core.util.InfraUtils;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
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
        if (os != null) {
            dump(bytes, os);
        }
    }

    public static byte[] dump(Class<?> clazz) throws Throwable {
        Instrumentation instrumentation = InfraUtils.getInstrumentation();

        ClassBytesDumpTransformer classDumpTransformer = new ClassBytesDumpTransformer(Collections.singleton(clazz), new File("E:\\test\\"));
        instrumentation.addTransformer(classDumpTransformer, true);

        HotRefresher.stop();
        try {
            instrumentation.retransformClasses(clazz);
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        } finally {
            instrumentation.removeTransformer(classDumpTransformer);
            HotRefresher.start();
        }

        return classDumpTransformer.getDumpResult().get(clazz);
    }

    public static void dump(byte[] bytes, String storePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(FileUtils.getFile(storePath))) {
            dump(bytes, fos);
        }
    }

    public static void dump(byte[] bytes, OutputStream os) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        FileUtils.safeWrite(bais, os);
    }
}
