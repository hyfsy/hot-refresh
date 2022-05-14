package com.hyf.hotrefresh.memory;

import com.hyf.hotrefresh.Constants;
import com.hyf.hotrefresh.util.IOUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class MemoryByteCodeTests {

    private static final String CLASS_NAME = "com.hyf.hotrefresh.classloader.Test";

    private MemoryByteCode memoryByteCode;
    private byte[]         content = "aaa".getBytes(Constants.MESSAGE_ENCODING);

    @Before
    public void before() {
        memoryByteCode = new MemoryByteCode(CLASS_NAME);
    }

    @Test
    public void testWriteAndRead() throws IOException {

        assertNull(memoryByteCode.getByteCode());
        assertEquals(CLASS_NAME, memoryByteCode.getClassName());

        try (ByteArrayInputStream bais = new ByteArrayInputStream(content);
             OutputStream os = memoryByteCode.openOutputStream()) {
            IOUtil.writeTo(bais, os);
        }

        assertArrayEquals(content, memoryByteCode.getByteCode());

        try (InputStream is = memoryByteCode.openInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtil.writeTo(is, baos);
            assertArrayEquals(content, baos.toByteArray());
        }
    }
}
