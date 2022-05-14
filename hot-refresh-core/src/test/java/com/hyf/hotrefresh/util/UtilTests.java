package com.hyf.hotrefresh.util;

import com.hyf.hotrefresh.memory.MemoryClassLoader;
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
        assertNotNull(Util.getThrowawayMemoryClassLoader());
        assertNotNull(Util.getInfrastructureJarClassLoader());
        assertNotNull(Util.getInstrumentation());
    }

    @Test
    public void testGetDifferentMemoryClassLoader() {
        MemoryClassLoader mcl = Util.getThrowawayMemoryClassLoader();
        MemoryClassLoader mcl2 = Util.getThrowawayMemoryClassLoader();
        assertNotEquals(mcl, mcl2);
    }

    @Test
    public void testCCLNotMemoryClassLoader() {
        assertFalse(Util.getOriginContextClassLoader() instanceof MemoryClassLoader);
    }
}
