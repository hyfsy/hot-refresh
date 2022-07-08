package com.hyf.hotrefresh.core.util;

import com.hyf.hotrefresh.common.util.ReflectionUtils;
import com.hyf.hotrefresh.core.agent.transform.JustForInvokeTransformer;
import com.hyf.hotrefresh.core.agent.transform.PilingTransformer;
import org.objectweb.asm.tree.ClassNode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.function.Consumer;

public abstract class TransformUtils {

    public static void signalTransformer(Class<?> clazz, Consumer<ClassNode> classNodeConsumer)
        throws UnmodifiableClassException {
        signalTransformer(Util.getInstrumentation(), clazz, classNodeConsumer, false);
    }

    public static void signalTransformer(Instrumentation instrumentation, Class<?> clazz,
        Consumer<ClassNode> classNodeConsumer, boolean ephemeral) throws UnmodifiableClassException {

        // for pilling code to use in transformer
        ClassLoader originClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(Util.getInfrastructureJarClassLoader());

        try {
            Class<?> transformerClass = InfraUtils.forName(PilingTransformer.class.getName());
            ClassFileTransformer transformer = (ClassFileTransformer)ReflectionUtils.newClassInstance(transformerClass,
                new Class[] {Class.class, Consumer.class}, clazz, classNodeConsumer);
            instrumentation.addTransformer(transformer, true);
            try {
                instrumentation.retransformClasses(clazz);
            } finally {
                if (ephemeral) {
                    instrumentation.removeTransformer(transformer);
                }
            }

            if (ephemeral) {
                instrumentation.addTransformer(JustForInvokeTransformer.INSTANCE, true);
                try {
                    instrumentation.retransformClasses(clazz);
                } finally {
                    instrumentation.removeTransformer(JustForInvokeTransformer.INSTANCE);
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(originClassLoader);
        }
    }

}
