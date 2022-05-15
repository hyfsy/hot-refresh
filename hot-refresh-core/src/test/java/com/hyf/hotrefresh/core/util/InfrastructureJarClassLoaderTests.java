package com.hyf.hotrefresh.core.util;

import com.hyf.hotrefresh.core.TestJavaFileUtils;
import org.junit.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class InfrastructureJarClassLoaderTests {

    private final String field = "test_field";

    private boolean get() {
        return false;
    }

    @Test
    public void testSimpleOperation() {
        InfrastructureJarClassLoader infra = Util.getInfrastructureJarClassLoader();
        assertNotNull(infra.getInstrumentation());
        assertNotNull(infra.getJavaCompiler());

        assertEquals(TestJavaFileUtils.getClassName(), infra.getClassName(TestJavaFileUtils.getClassBytes()));

        Method getMethod = infra.getMethod(InfrastructureJarClassLoaderTests.class, "get");
        assertNotNull(getMethod);
        Field fieldField = infra.getField(InfrastructureJarClassLoaderTests.class, "field");
        assertNotNull(fieldField);
        assertNotNull(infra.invokeMethod(getMethod, this));
        assertNotNull(infra.invokeField(fieldField, this));
    }

    // TODO current not support register class file

    // @Test(expected = ClassNotFoundException.class)
    public void testNotRegisterInfrastructureJar() throws ClassNotFoundException {
        Util.getOriginContextClassLoader().loadClass(TestJavaFileUtils.getClassName());
    }

    // @Test
    public void testRegisterInfrastructureJar() throws ClassNotFoundException {
        Util.getInfrastructureJarClassLoader().registerInfrastructureJar("test", "Test.class");
        Util.getInfrastructureJarClassLoader().forName(TestJavaFileUtils.getClassName());
    }

    @Test
    public void testJavaCompilerLoad() {
        InfrastructureJarClassLoader infra = Util.getInfrastructureJarClassLoader();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertEquals(systemClassLoaderLoaded(compiler), infra.canLoad(compiler.getClass()));

        assertTrue(infra.canLoad(InfrastructureJarClassLoaderTests.class));

        JavaCompiler compiler2 = infra.getJavaCompiler();
        assertTrue(infra.canLoad(compiler2.getClass()));

        assertEquals(systemClassLoaderLoaded(compiler), compiler == compiler2);
    }

    private boolean systemClassLoaderLoaded(JavaCompiler compiler) {
        return compiler.getClass().getClassLoader() == ClassLoader.getSystemClassLoader();
    }
}
