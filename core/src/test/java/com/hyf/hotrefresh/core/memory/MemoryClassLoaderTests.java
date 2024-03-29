package com.hyf.hotrefresh.core.memory;

import com.hyf.hotrefresh.core.TestJavaFileUtils;
import com.hyf.hotrefresh.core.exception.CompileException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class MemoryClassLoaderTests {

    public static final String CLASS_NAME = TestJavaFileUtils.getClassName();

    private Map<String, byte[]> compiledBytes;

    @Before
    public void before() throws CompileException {
        MemoryCode memoryCode = new MemoryCode(TestJavaFileUtils.getFileName(), TestJavaFileUtils.getContent());
        compiledBytes = MemoryCodeCompiler.compile(memoryCode);
    }

    @Test
    public void testCompiledContentOperate() {
        MemoryClassLoader memoryClassLoader = new MemoryClassLoader(null);

        assertNull(memoryClassLoader.get(CLASS_NAME));
        memoryClassLoader.store(compiledBytes);
        assertNotNull(memoryClassLoader.get(CLASS_NAME));
        memoryClassLoader.remove(CLASS_NAME);
        assertNull(memoryClassLoader.get(CLASS_NAME));
        List<Class<?>> classList = memoryClassLoader.clear();
        assertEquals(0, classList.size());
    }

    @Test(expected = ClassNotFoundException.class)
    public void testNonLoadClass() throws ClassNotFoundException {
        new MemoryClassLoader(null).loadClass(CLASS_NAME);
    }

    @Test
    public void testLoadClass() throws ClassNotFoundException {
        MemoryClassLoader memoryClassLoader = new MemoryClassLoader(null);
        memoryClassLoader.store(compiledBytes);
        assertNotNull(memoryClassLoader.loadClass(CLASS_NAME));
    }
}
