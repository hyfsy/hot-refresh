package com.hyf.hotrefresh.refresh;

import com.hyf.hotrefresh.TestJavaFileUtils;
import com.hyf.hotrefresh.exception.CompileException;
import com.hyf.hotrefresh.memory.MemoryClassLoader;
import com.hyf.hotrefresh.memory.MemoryCode;
import com.hyf.hotrefresh.memory.MemoryCodeCompiler;
import org.junit.Test;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class HotRefreshTransformerTests {

    @Test
    public void testTransform() throws IllegalClassFormatException, CompileException {

        HotRefreshTransformer hotRefreshTransformer = new HotRefreshTransformer(MemoryClassLoader.newInstance());
        byte[] bytes = new byte[]{1};
        byte[] transformedBytes = hotRefreshTransformer.transform(null, TestJavaFileUtils.getClassName().replace(".", "/"), null, null, bytes);

        assertEquals(bytes, transformedBytes);

        MemoryClassLoader memoryClassLoader = MemoryClassLoader.newInstance();
        MemoryCode memoryCode = new MemoryCode(TestJavaFileUtils.getFileName(), TestJavaFileUtils.getContent());
        Map<String, byte[]> compiledBytes = MemoryCodeCompiler.compile(memoryCode);
        memoryClassLoader.store(compiledBytes);

        transformedBytes = hotRefreshTransformer.transform(null, TestJavaFileUtils.getClassName().replace(".", "/"), null, null, bytes);
        assertNotEquals(bytes, transformedBytes);
    }
}
