package com.hyf.hotrefresh.core.memory;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.core.TestJavaFileUtils;
import com.hyf.hotrefresh.core.exception.CompileException;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class MemoryByteCodeCollectClassLoaderTests {

    private static final String CLASS_NAME = TestJavaFileUtils.getClassName();

    private MemoryByteCodeCollectClassLoader classLoader;
    private MemoryByteCode                   memoryByteCode;

    @Before
    public void before() {
        classLoader = new MemoryByteCodeCollectClassLoader();
        memoryByteCode = new MemoryByteCode(CLASS_NAME);
    }

    @Test
    public void testMemoryByteCodeCollect() throws IOException {

        assertNull(classLoader.get(CLASS_NAME));
        classLoader.collect(memoryByteCode);
        assertNotNull(classLoader.get(CLASS_NAME));

        assertNull(classLoader.getCollectedByteCodes().get(CLASS_NAME));

        byte[] content = "aaa".getBytes(Constants.MESSAGE_ENCODING);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(content);
             OutputStream os = memoryByteCode.openOutputStream()) {
            IOUtils.writeTo(bais, os);
        }

        assertNotNull(classLoader.getCollectedByteCodes().get(CLASS_NAME));
    }

    @Test(expected = ClassNotFoundException.class)
    public void testLoadNonCollectedClass() throws ClassNotFoundException {
        classLoader.loadClass(CLASS_NAME);
    }

    @Test
    public void testLoadCollectedClass() throws IOException, ClassNotFoundException, CompileException {

        MemoryCode memoryCode = new MemoryCode(TestJavaFileUtils.getFileName(), TestJavaFileUtils.getContent());
        Map<String, byte[]> compiledBytes = MemoryCodeCompiler.compile(memoryCode);

        byte[] content = compiledBytes.get(TestJavaFileUtils.getClassName());
        try (ByteArrayInputStream bais = new ByteArrayInputStream(content);
             OutputStream os = memoryByteCode.openOutputStream()) {
            IOUtils.writeTo(bais, os);
        }
        classLoader.collect(memoryByteCode);
        classLoader.loadClass(CLASS_NAME);
    }

}
