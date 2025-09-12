package com.hyf.hotrefresh.core.memory.groovy;

import com.hyf.hotrefresh.common.util.FastReflectionUtils;
import com.hyf.hotrefresh.core.TestJavaFileUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class GroovyCodeCompilerTests {

    @Test
    public void testCompile() {
        Class<?> klass = GroovyCodeCompiler.compile(TestJavaFileUtils.getContent());
        boolean result = FastReflectionUtils.fastInvokeMethod(klass, "get");
        assertFalse(result);
    }

    @Test
    public void testCompileSameName() {
        Class<?> klass = GroovyCodeCompiler.compile(TestJavaFileUtils.getContent());
        Class<?> klass2 = GroovyCodeCompiler.compile(TestJavaFileUtils.getContent());
        assertNotEquals(klass, klass2);
        assertNotEquals(klass.getClassLoader(), klass2.getClassLoader());
    }
}
