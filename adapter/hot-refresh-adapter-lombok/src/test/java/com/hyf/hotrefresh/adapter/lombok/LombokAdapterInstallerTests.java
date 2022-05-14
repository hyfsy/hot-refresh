package com.hyf.hotrefresh.adapter.lombok;

import com.hyf.hotrefresh.memory.AnnotationProcessorCompositeClassLoader;
import com.hyf.hotrefresh.util.InfrastructureJarClassLoader;
import com.hyf.hotrefresh.util.Util;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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

    @Test(expected = ClassNotFoundException.class)
    public void testCannotLoadAnnotationProcessor() throws ClassNotFoundException {
        String processorClass = "lombok.launch.AnnotationProcessorHider$AnnotationProcessor";

        List<ClassLoader> classLoaders = AnnotationProcessorCompositeClassLoader.getInstance().getClassLoaders();
        classLoaders.removeIf(cl -> cl instanceof LombokShadowClassLoaderDelegate);

        AnnotationProcessorCompositeClassLoader.getInstance().loadClass(processorClass);
    }

    @Test
    public void testCanLoadAnnotationProcessor() throws ClassNotFoundException {
        String processorClass = "lombok.launch.AnnotationProcessorHider$AnnotationProcessor";
        Class<?> clazz = AnnotationProcessorCompositeClassLoader.getInstance().loadClass(processorClass);

        ClassLoader shadowClassLoader = installer.getShadowClassLoader();
        assertEquals(clazz.getClassLoader(), shadowClassLoader);
    }

}
