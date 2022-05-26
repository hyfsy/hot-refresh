package com.hyf.hotrefresh.core.classloader;

import com.hyf.hotrefresh.core.TestJavaFileUtils;
import com.hyf.hotrefresh.core.util.InfraUtils;
import com.hyf.hotrefresh.core.util.Util;
import org.junit.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class InfrastructureJarClassLoaderTests {

    // TODO current not support register class file

    // @Test(expected = ClassNotFoundException.class)
    public void testNotRegisterInfrastructureJar() throws ClassNotFoundException {
        Util.getOriginContextClassLoader().loadClass(TestJavaFileUtils.getClassName());
    }

    // @Test
    public void testRegisterInfrastructureJar() throws ClassNotFoundException {
        Util.getInfrastructureJarClassLoader().registerInfrastructureJar("test", "Test.class");
        InfraUtils.forName(TestJavaFileUtils.getClassName());
    }
}
