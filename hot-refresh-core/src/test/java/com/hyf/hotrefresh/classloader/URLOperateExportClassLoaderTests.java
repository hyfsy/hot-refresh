package com.hyf.hotrefresh.classloader;

import org.junit.Test;
import sun.misc.URLClassPath;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.Assert.assertEquals;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class URLOperateExportClassLoaderTests {

    @Test
    public void testAddPath() throws Exception {

        URLOperateExportClassLoader classLoader = new URLOperateExportClassLoader(new URL[]{});

        Field field = URLClassLoader.class.getDeclaredField("ucp");
        field.setAccessible(true);
        URLClassPath ucp = (URLClassPath) field.get(classLoader);

        assertEquals(0, ucp.getURLs().length);
        classLoader.addPath("Test.java");
        assertEquals(1, ucp.getURLs().length);
    }
}
