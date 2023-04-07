package com.hyf.hotrefresh.core.agent.transform;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.core.util.Util;
import com.hyf.hotrefresh.shadow.infrastructure.Infrastructure;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Infrastructure
public class PilingTransformer implements ClassFileTransformer {

    private final List<String>        classResourceNames;
    private final Consumer<ClassNode> classNodeConsumer;

    public PilingTransformer(Class<?> clazz, Consumer<ClassNode> classNodeConsumer) {
        this.classResourceNames = Collections.singletonList(clazz.getName().replace(".", "/"));
        this.classNodeConsumer = classNodeConsumer;
    }

    public PilingTransformer(String className, Consumer<ClassNode> classNodeConsumer) {
        this.classResourceNames = Collections.singletonList(className.replace(".", "/"));
        this.classNodeConsumer = classNodeConsumer;
    }

    public PilingTransformer(List<String> classNames, Consumer<ClassNode> classNodeConsumer) {
        this.classResourceNames = classNames.stream().map(name -> name.replace(".", "/")).collect(Collectors.toList());
        this.classNodeConsumer = classNodeConsumer;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (!classResourceNames.contains(className)) {
            return classfileBuffer;
        }

        try {
            byte[] bytes = IOUtils.readAsByteArray(Util.getOriginContextClassLoader().getResourceAsStream(className + ".class"), true);
            ClassReader reader = new ClassReader(bytes);
            ClassNode classNode = new ClassNode(Opcodes.ASM5);
            reader.accept(classNode, ClassReader.SKIP_FRAMES);

            classNodeConsumer.accept(classNode);

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            byte[] newBytes = writer.toByteArray();
            // ClassBytesDumper.dump(newBytes, "E:\\AAA.class");
            return newBytes;
        } catch (Throwable t) {
            Log.error("Failed to use instrumentation piling class", t);
            return classfileBuffer;
        }
    }
}
