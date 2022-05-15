package com.hyf.hotrefresh.core.refresh;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.core.memory.MemoryClassLoader;
import com.hyf.hotrefresh.common.util.IOUtils;

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

    public static final String DEBUG_STORE_PATH = Log.LOG_HOME + File.separator + "debug";

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
        else {
            if (Log.isDebugMode()) {
                Log.debug("Hot refresh transform class: " + fullClassName);
                store(classResourceName, bytes);
            }
        }

        return bytes;
    }

    private void store(String classResourceName, byte[] bytes) {
        String filePath = DEBUG_STORE_PATH + File.separator + classResourceName.replace("/", File.separator) + ".class";
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             FileOutputStream fos = new FileOutputStream(filePath)) {
            IOUtils.writeTo(bais, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
