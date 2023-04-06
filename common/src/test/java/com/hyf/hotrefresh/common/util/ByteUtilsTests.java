package com.hyf.hotrefresh.common.util;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author baB_hyf
 * @date 2022/05/28
 */
public class ByteUtilsTests {

    @Test(expected = IllegalArgumentException.class)
    public void testParse() throws Exception {
        Path hotrefresh = Files.createTempFile(null, null);
        try (FileOutputStream fos = new FileOutputStream(hotrefresh.toFile())) {
            byte[] parse = ByteUtils.parse(getS());
            IOUtils.writeTo(new ByteArrayInputStream(parse), fos);
        } finally {
            Files.delete(hotrefresh);
        }
    }

    public String getS() {
        return "";
    }
}
