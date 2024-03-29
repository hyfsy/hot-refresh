package com.hyf.hotrefresh.core.refresh;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.Services;
import com.hyf.hotrefresh.core.extend.ClassBytesDumper;
import com.hyf.hotrefresh.core.memory.MemoryClassLoader;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
class HotRefreshTransformer implements ClassFileTransformer {

    public static final String DEBUG_STORE_PATH = Log.LOG_HOME + File.separator + "debug";

    private final MemoryClassLoader placeHolderMemoryClassLoader;

    private final List<HotRefreshTransformPostProcessor> processors;

    public HotRefreshTransformer(MemoryClassLoader placeHolderMemoryClassLoader) {
        this.placeHolderMemoryClassLoader = placeHolderMemoryClassLoader;
        this.processors = Services.gets(HotRefreshTransformPostProcessor.class);
    }

    @Override
    public byte[] transform(ClassLoader loader, String classResourceName, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classFileBuffer) throws IllegalClassFormatException {

        if (classResourceName == null) {
            return classFileBuffer;
        }

        String fullClassName = classResourceName.replace("/", ".");

        byte[] bytes = placeHolderMemoryClassLoader.get(fullClassName);
        if (bytes == null) {
            bytes = classFileBuffer;
        }
        else {
            bytes = postTransform(loader, classResourceName, classBeingRedefined, protectionDomain, classFileBuffer, bytes);
            if (Log.isDebugMode()) {
                Log.debug("Hot refresh transform class: " + fullClassName);
                store(classResourceName, classBeingRedefined, classFileBuffer, bytes);
            }
        }

        return bytes;
    }

    private byte[] postTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer, byte[] memoryClassfileBuffer) throws IllegalClassFormatException {
        for (HotRefreshTransformPostProcessor processor : processors) {
            byte[] postTransformedBytes = processor.postTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer, memoryClassfileBuffer);
            if (postTransformedBytes != null) {
                memoryClassfileBuffer = postTransformedBytes;
            }
        }
        return memoryClassfileBuffer;
    }

    private void store(String classResourceName, Class<?> classBeingRedefined, byte[] transformedBytes, byte[] memoryBytes) {

        byte[] current = null;
        byte[] transformed = null;
        if (memoryBytes != null) {
            String classFilePath = DEBUG_STORE_PATH + File.separator + classResourceName.replace("/", File.separator) + ".class";
            try {
                ClassBytesDumper.dump(memoryBytes, classFilePath);
                current = memoryBytes;
            } catch (IOException e) {
                Log.error("Failed to dump bytes", e);
            }
        }
        if (transformedBytes != null) {
            String classFilePath = DEBUG_STORE_PATH + File.separator + classResourceName.replace("/", File.separator) + "_TRANSFORMED.class";
            try {
                ClassBytesDumper.dump(transformedBytes, classFilePath);
                transformed = transformedBytes;
            } catch (IOException e) {
                Log.error("Failed to dump bytes", e);
            }
        }

        // let outside exception throw points to get if this class failed to reTransform
        ReTransformExceptionRecorder.record(classBeingRedefined, current, transformed);
    }
}
