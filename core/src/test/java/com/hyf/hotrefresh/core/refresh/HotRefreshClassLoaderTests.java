package com.hyf.hotrefresh.core.refresh;

import com.hyf.hotrefresh.core.util.Util;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.Assert.*;

/**
 * @author baB_hyf
 * @date 2023/04/05
 */
public class HotRefreshClassLoaderTests {

    @Test
    public void testBind() {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        HotRefreshClassLoader.bind();
        assertNotEquals(ccl, Thread.currentThread().getContextClassLoader());
        HotRefreshClassLoader.unBind();
        assertEquals(ccl, Thread.currentThread().getContextClassLoader());
    }

    @Test
    public void testAddOutputHome() {
        Util.getThrowawayHotRefreshClassLoader();
        ClassLoader scl = ClassLoader.getSystemClassLoader();
        if (!(scl instanceof URLClassLoader) || !scl.getClass().getName().contains("App")) {
            throw new IllegalStateException();
        }

        URLClassLoader urlClassLoader = (URLClassLoader) scl;
        URL[] urls = urlClassLoader.getURLs();
        String outputHome = ClassFileStorage.OUTPUT_HOME;
        boolean matchOutputHome = false;
        for (URL url : urls) {
            String s = url.getPath();
            if (new File(s).getAbsolutePath().equals(new File(outputHome).getAbsolutePath())) {
                matchOutputHome = true;
                break;
            }
        }
        assertTrue(matchOutputHome);
    }
}
