package com.hyf.hotrefresh.adapter.lombok;

import com.hyf.hotrefresh.install.CoreInstaller;
import com.hyf.hotrefresh.memory.AnnotationProcessorCompositeClassLoader;
import org.junit.Test;

public class ClassLoaderTests {

    @Test
    public void testAnnotationProcessorClassLoaderLoadLombok() throws ClassNotFoundException {
        String loadClass = "lombok.launch.AnnotationProcessorHider$AnnotationProcessor";

        System.out.println(CoreInstaller.install());

        AnnotationProcessorCompositeClassLoader.getInstance().loadClass(loadClass);
    }
}
