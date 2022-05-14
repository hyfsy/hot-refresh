package com.hyf.hotrefresh.extend;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClassBytesDumpTransformer implements ClassFileTransformer {

    private Set<Class<?>>         classesToEnhance;
    private Map<Class<?>, byte[]> dumpResult;

    private File directory;

    public ClassBytesDumpTransformer(Set<Class<?>> classesToEnhance, File directory) {
        this.classesToEnhance = classesToEnhance;
        this.dumpResult = new HashMap<>();
        this.directory = directory;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {
        try {
            if (classesToEnhance.contains(classBeingRedefined)) {
                dumpClassIfNecessary(classBeingRedefined, classfileBuffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<Class<?>, byte[]> getDumpResult() {
        return dumpResult;
    }

    private void dumpClassIfNecessary(Class<?> clazz, byte[] data) {

        // 创建类所在的包路径
        if (!directory.mkdirs() && !directory.exists()) {
            return;
        }

        dumpResult.put(clazz, data);
    }
}
