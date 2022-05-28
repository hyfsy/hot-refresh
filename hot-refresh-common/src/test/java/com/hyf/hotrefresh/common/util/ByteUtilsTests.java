package com.hyf.hotrefresh.common.util;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;

/**
 * @author baB_hyf
 * @date 2022/05/28
 */
public class ByteUtilsTests {

    @Test
    public void testParse() throws Exception {
        String filePath = "E:\\G.class";
        byte[] parse = ByteUtils.parse(getS());
        IOUtils.writeTo(new ByteArrayInputStream(parse), new FileOutputStream(filePath));
    }

    public String getS() {
        return "";
    }
}
