package com.hyf.hotrefresh.core.memory;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.core.TestJavaFileUtils;
import com.hyf.hotrefresh.core.TestJavaModel;
import com.hyf.hotrefresh.core.exception.CompileException;
import com.hyf.hotrefresh.core.util.Util;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class MemoryByteCodeCollectClassLoaderTests {

    private final TestJavaModel javaModel = new TestJavaModel();

    private MemoryByteCodeCollectClassLoader classLoader;
    private MemoryByteCode                   memoryByteCode;

    @Before
    public void before() {
        classLoader = new MemoryByteCodeCollectClassLoader(MemoryClassLoader.newInstance(Util.getInfrastructureJarClassLoader()));
        memoryByteCode = new MemoryByteCode(javaModel.getClassName());
    }

    @Test
    public void testMemoryByteCodeCollect() throws IOException {

        assertNull(classLoader.get(javaModel.getClassName()));
        classLoader.collect(memoryByteCode);
        assertNotNull(classLoader.get(javaModel.getClassName()));

        assertNull(classLoader.getCollectedByteCodes().get(javaModel.getClassName()));

        byte[] content = "aaa".getBytes(Constants.MESSAGE_ENCODING);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(content);
             OutputStream os = memoryByteCode.openOutputStream()) {
            IOUtils.writeTo(bais, os);
        }

        assertNotNull(classLoader.getCollectedByteCodes().get(javaModel.getClassName()));
    }

    @Test(expected = ClassNotFoundException.class)
    public void testLoadNonCollectedClass() throws ClassNotFoundException {
        classLoader.loadClass(javaModel.getClassName());
    }

    @Test
    public void testLoadCollectedClass() throws IOException, ClassNotFoundException, CompileException {

        MemoryCode memoryCode = new MemoryCode(javaModel.getFileName(), javaModel.getContent());
        Map<String, byte[]> compiledBytes = MemoryCodeCompiler.compile(memoryCode);

        byte[] content = compiledBytes.get(javaModel.getClassName());
        try (ByteArrayInputStream bais = new ByteArrayInputStream(content);
             OutputStream os = memoryByteCode.openOutputStream()) {
            IOUtils.writeTo(bais, os);
        }
        classLoader.collect(memoryByteCode);
        classLoader.loadClass(javaModel.getClassName());
    }

}
