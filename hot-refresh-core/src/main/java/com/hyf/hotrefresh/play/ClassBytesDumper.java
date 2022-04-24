package com.hyf.hotrefresh.play;

import com.hyf.hotrefresh.Util;
import com.hyf.hotrefresh.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Collections;

/**
 * @author baB_hyf
 * @date 2022/01/27
 */
public class ClassBytesDumper {

    public static void dump(Class<?> clazz, String storePath) {
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
        dump(bytes, storePath);
    }

    public static void dump(byte[] bytes, String storePath) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            FileUtil.safeWrite(new File(storePath), bais);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
