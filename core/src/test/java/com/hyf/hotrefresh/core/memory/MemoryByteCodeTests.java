package com.hyf.hotrefresh.core.memory;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.util.IOUtils;
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

    private final byte[]         content = "aaa".getBytes(Constants.MESSAGE_ENCODING);
    private       MemoryByteCode memoryByteCode;

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
            IOUtils.writeTo(bais, os);
        }

        assertArrayEquals(content, memoryByteCode.getByteCode());

        try (InputStream is = memoryByteCode.openInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtils.writeTo(is, baos);
            assertArrayEquals(content, baos.toByteArray());
        }
    }
}
