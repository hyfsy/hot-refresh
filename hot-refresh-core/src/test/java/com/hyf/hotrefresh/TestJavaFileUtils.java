package com.hyf.hotrefresh;

import com.hyf.hotrefresh.util.IOUtil;
import com.hyf.hotrefresh.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class TestJavaFileUtils {

    public static String getClassName() {
        return "com.hyf.hotrefresh.Test";
    }

    public static String getFileName() {
        return "Test.java";
    }

    public static String getClassFileName() {
        return "Test.class";
    }

    public static String getContent() {
        try (InputStream is = Util.getOriginContextClassLoader().getResourceAsStream(getFileName());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtil.writeTo(is, baos);
            return baos.toString(Constants.MESSAGE_ENCODING.name());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getReplacedContent() {
        String content = getContent();
        return content.replace("return false", "return true");
    }

    public static byte[] getClassBytes() {
        try (InputStream is = Util.getOriginContextClassLoader().getResourceAsStream(getClassFileName());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtil.writeTo(is, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
