package com.hyf.hotrefresh.memory;

import com.hyf.hotrefresh.TestJavaFileUtils;
import com.hyf.hotrefresh.exception.CompileException;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class MemoryCodeCompilerTests {

    @Test
    public void testCompile() throws CompileException {
        MemoryCode memoryCode = new MemoryCode(TestJavaFileUtils.getFileName(), TestJavaFileUtils.getContent());
        Map<String, byte[]> compile = MemoryCodeCompiler.compile(memoryCode);
        assertFalse(compile.isEmpty());

        Set<MemoryCode> memoryCodes = new HashSet<>(Arrays.asList(memoryCode, memoryCode));
        compile = MemoryCodeCompiler.compile(memoryCodes);
        assertFalse(compile.isEmpty());
        assertEquals(1, compile.size());
    }

    @Test
    public void testInitOptions() {
        List<String> compileOptions = MemoryCodeCompiler.getCompileOptions();
        assertTrue(compileOptions.contains("-Aaaa=aaa"));
        assertTrue(compileOptions.contains("-Abbb=bbb"));
        assertTrue(compileOptions.contains("-Accc=ccc"));
        assertFalse(compileOptions.contains(""));
        assertFalse(compileOptions.contains(null));
    }
}
