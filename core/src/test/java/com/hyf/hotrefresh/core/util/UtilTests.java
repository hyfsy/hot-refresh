package com.hyf.hotrefresh.core.util;

import com.hyf.hotrefresh.core.memory.MemoryClassLoader;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class UtilTests {

    @Test
    public void testGetNotNull() {
        assertNotNull(Util.getOriginContextClassLoader());
        assertNotNull(Util.getThrowawayHotRefreshClassLoader());
        assertNotNull(Util.getInfrastructureJarClassLoader());
    }

    @Test
    public void testGetDifferentMemoryClassLoader() {
        MemoryClassLoader mcl = Util.getThrowawayHotRefreshClassLoader();
        MemoryClassLoader mcl2 = Util.getThrowawayHotRefreshClassLoader();
        assertNotEquals(mcl, mcl2);
    }

    @Test
    public void testCCLNotMemoryClassLoader() {
        assertFalse(Util.getOriginContextClassLoader() instanceof MemoryClassLoader);
    }

    @Test
    public void testBootstrapClassLoader() {
        Thread.currentThread().setContextClassLoader(null);
        assertNull(Util.getOriginContextClassLoader());
    }

    @Test
    public void testSingletonHotRefreshClassLoader() {
        MemoryClassLoader mcl = Util.getHotRefreshClassLoader();
        MemoryClassLoader mcl2 = Util.getHotRefreshClassLoader();
        assertEquals(mcl, mcl2);
    }

}
