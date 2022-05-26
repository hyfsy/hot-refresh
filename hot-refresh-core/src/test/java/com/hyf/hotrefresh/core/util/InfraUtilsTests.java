package com.hyf.hotrefresh.core.util;

import com.hyf.hotrefresh.core.TestJavaFileUtils;
import com.hyf.hotrefresh.core.classloader.InfrastructureJarClassLoaderTests;
import org.junit.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import static org.junit.Assert.*;

/**
 * @author baB_hyf
 * @date 2022/05/26
 */
public class InfraUtilsTests {

    @Test
    public void testSimpleOperation() {
        assertNotNull(InfraUtils.getInstrumentation());
        assertNotNull(InfraUtils.getJavaCompiler());

        assertEquals(TestJavaFileUtils.getClassName(), InfraUtils.getClassName(TestJavaFileUtils.getClassBytes()));
    }

    @Test
    public void testJavaCompilerLoad() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertEquals(systemClassLoaderLoaded(compiler), InfraUtils.canLoad(compiler.getClass()));

        assertTrue(InfraUtils.canLoad(InfrastructureJarClassLoaderTests.class));

        JavaCompiler compiler2 = InfraUtils.getJavaCompiler();
        assertTrue(InfraUtils.canLoad(compiler2.getClass()));

        assertEquals(systemClassLoaderLoaded(compiler), compiler == compiler2);
    }

    private boolean systemClassLoaderLoaded(JavaCompiler compiler) {
        return compiler.getClass().getClassLoader() == ClassLoader.getSystemClassLoader();
    }
}
