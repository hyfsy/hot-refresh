package com.hyf.hotrefresh.common.util;

import com.hyf.hotrefresh.common.Constants;

import java.io.*;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public abstract class IOUtils {

    public static byte[] readAsByteArray(InputStream is) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            writeTo(is, baos);
            return baos.toByteArray();
        }
    }

    public static String readAsString(InputStream is) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            writeTo(is, baos);
            return baos.toString(Constants.MESSAGE_ENCODING.name());
        }
    }

    public static void writeTo(InputStream is, OutputStream os) throws IOException {

        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(os);

        int len;
        byte[] bytes = new byte[1024];
        while ((len = bis.read(bytes)) != -1) {
            bos.write(bytes, 0, len);
        }

        bos.flush();
    }

    public static void close(Closeable... closeable) {
        for (Closeable c : closeable) {
            try {
                c.close();
            } catch (IOException ignored) {
            }
        }
    }
}
