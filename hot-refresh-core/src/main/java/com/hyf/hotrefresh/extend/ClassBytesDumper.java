package com.hyf.hotrefresh.extend;

import com.hyf.hotrefresh.util.FileUtil;
import com.hyf.hotrefresh.util.Util;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Collections;

/**
 * @author baB_hyf
 * @date 2022/01/27
 */
public class ClassBytesDumper {

    public static void dump(Class<?> clazz, String storePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(storePath)) {
            dump(clazz, fos);
        }
    }

    public static void dump(Class<?> clazz, OutputStream os) {
        Instrumentation instrumentation = Util.getInfrastructureJarClassLoader().getInstrumentation();

        ClassBytesDumpTransformer classDumpTransformer = new ClassBytesDumpTransformer(Collections.singleton(clazz), new File("E:\\test\\"));
        instrumentation.addTransformer(classDumpTransformer, true);

        try {
            instrumentation.retransformClasses(clazz);
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        } finally {
            instrumentation.removeTransformer(classDumpTransformer);
        }

        byte[] bytes = classDumpTransformer.getDumpResult().get(clazz);
        dump(bytes, os);
    }

    public static void dump(byte[] bytes, String storePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(storePath)) {
            dump(bytes, fos);
        }
    }

    public static void dump(byte[] bytes, OutputStream os) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        FileUtil.safeWrite(bais, os);
    }
}
