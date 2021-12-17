package com.hyf.hotrefresh;

import com.hyf.hotrefresh.memory.MemoryClassLoader;
import com.hyf.hotrefresh.util.IOUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
class HotRefreshTransformer implements ClassFileTransformer {

    private final MemoryClassLoader placeHolderMemoryClassLoader;

    public HotRefreshTransformer(MemoryClassLoader placeHolderMemoryClassLoader) {
        this.placeHolderMemoryClassLoader = placeHolderMemoryClassLoader;
    }

    @Override
    public byte[] transform(ClassLoader loader, String classResourceName, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (classResourceName == null) {
            return classfileBuffer;
        }

        String fullClassName = classResourceName.replace("/", ".");

        byte[] bytes = placeHolderMemoryClassLoader.get(fullClassName);
        if (bytes == null) {
            bytes = classfileBuffer;
        }
        // else {
        //     store(classResourceName, bytes);
        // }

        return bytes;
    }

    private void store(String classResourceName, byte[] bytes) {
        String filePath = "E:\\" + classResourceName.replace("/", File.separator) + ".class";
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             FileOutputStream fos = new FileOutputStream(filePath)) {
            IOUtil.writeTo(bais, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
