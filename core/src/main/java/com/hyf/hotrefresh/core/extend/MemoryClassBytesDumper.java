package com.hyf.hotrefresh.core.extend;

import com.hyf.hotrefresh.common.util.FileUtils;
import com.hyf.hotrefresh.core.util.Util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author baB_hyf
 * @date 2022/05/27
 */
public class MemoryClassBytesDumper {

    public static void dump(String className, String storePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(FileUtils.getFile(storePath))) {
            dump(className, fos);
        }
    }

    public static void dump(String className, OutputStream os) throws IOException {
        byte[] bytes = Util.getThrowawayMemoryClassLoader().get(className);
        if (bytes != null) {
            os.write(bytes);
            os.flush();
        }
    }
}
