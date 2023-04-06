package com.hyf.hotrefresh.core.refresh;

import com.hyf.hotrefresh.core.TestJavaFileUtils;
import com.hyf.hotrefresh.core.exception.CompileException;
import com.hyf.hotrefresh.core.memory.MemoryCode;
import com.hyf.hotrefresh.core.memory.MemoryCodeCompiler;
import com.hyf.hotrefresh.core.util.Util;
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

        HotRefreshTransformer hotRefreshTransformer = new HotRefreshTransformer(Util.getThrowawayHotRefreshClassLoader());
        byte[] bytes = new byte[]{1};
        byte[] transformedBytes = hotRefreshTransformer.transform(null, TestJavaFileUtils.getClassName().replace(".", "/"), null, null, bytes);

        assertEquals(bytes, transformedBytes);

        HotRefreshClassLoader memoryClassLoader = HotRefreshClassLoader.newInstance();
        MemoryCode memoryCode = new MemoryCode(TestJavaFileUtils.getFileName(), TestJavaFileUtils.getContent());
        Map<String, byte[]> compiledBytes = MemoryCodeCompiler.compile(memoryCode);
        memoryClassLoader.store(compiledBytes);

        transformedBytes = hotRefreshTransformer.transform(null, TestJavaFileUtils.getClassName().replace(".", "/"), null, null, bytes);
        assertNotEquals(bytes, transformedBytes);
        memoryClassLoader.clear();
    }
}
