package com.hyf.hotrefresh;

import com.hyf.hotrefresh.util.InfrastructureJarClassLoader;
import com.hyf.hotrefresh.util.Util;
import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import static org.junit.jupiter.api.Assertions.*;

public class ClassLoaderTests {

    @Test
    public void testAnnotationProcessorClassLoader() {

        InfrastructureJarClassLoader ifjcl = Util.getInfrastructureJarClassLoader();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        System.out.println(compiler.getClass().getClassLoader());

        System.out.println(ifjcl.canLoad(compiler.getClass()));
        assertTrue(ifjcl.canLoad(ClassLoaderTests.class));

        JavaCompiler compiler2 = ifjcl.getJavaCompiler();

        System.out.println(compiler == compiler2);
        System.out.println(compiler2.getClass().getClassLoader());

    }
}
