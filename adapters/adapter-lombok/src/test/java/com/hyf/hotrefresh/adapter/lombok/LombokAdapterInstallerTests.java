package com.hyf.hotrefresh.adapter.lombok;

import com.hyf.hotrefresh.core.classloader.InfrastructureJarClassLoader;
import com.hyf.hotrefresh.core.memory.AnnotationProcessorCompositeClassLoader;
import com.hyf.hotrefresh.core.util.Util;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author baB_hyf
 * @date 2022/05/13
 */
public class LombokAdapterInstallerTests {

    private LombokAdapterInstaller installer;

    @Before
    public void before() {
        installer = new LombokAdapterInstaller();
        installer.install();
    }

    @Test
    public void testReplaceParent() {
        ClassLoader cl = installer.getShadowClassLoader().getClass().getClassLoader();
        assertEquals(cl, Util.getInfrastructureJarClassLoader());
    }

    @Test
    public void testLoadLombokClass() throws ClassNotFoundException {
        InfrastructureJarClassLoader.getInstance().loadClass("lombok.launch.Main");
    }

    @Test
    public void testCanLoadAnnotationProcessor() throws ClassNotFoundException {
        String processorClass = "lombok.launch.AnnotationProcessorHider$AnnotationProcessor";
        Class<?> clazz = AnnotationProcessorCompositeClassLoader.getInstance().loadClass(processorClass);

        ClassLoader classLoader = Util.getInfrastructureJarClassLoader();
        assertEquals(clazz.getClassLoader(), classLoader);
    }

}
