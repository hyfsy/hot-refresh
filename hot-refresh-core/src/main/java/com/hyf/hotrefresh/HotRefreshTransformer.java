package com.hyf.hotrefresh;

import com.hyf.hotrefresh.memory.MemoryClassLoader;

import java.io.*;
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

        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("E:\\" + classResourceName.replace("/", File.separator) + ".class"))) {

            int len;
            byte[] buf = new byte[1024];
            while ((len = bais.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            bos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
