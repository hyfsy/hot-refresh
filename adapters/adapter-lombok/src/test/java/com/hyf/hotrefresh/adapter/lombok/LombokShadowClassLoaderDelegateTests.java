package com.hyf.hotrefresh.adapter.lombok;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author baB_hyf
 * @date 2022/05/13
 */
@RunWith(MockitoJUnitRunner.class)
public class LombokShadowClassLoaderDelegateTests {

    private LombokShadowClassLoaderDelegate delegate;

    @Mock
    private ClassLoader classLoader;

    @Before
    public void before() {
        delegate = new LombokShadowClassLoaderDelegate(classLoader);
    }

    @Test
    public void testLoadCondition() {
        assertTrue(delegate.loadCondition("lombok.launch.Main"));
        assertFalse(delegate.loadCondition("java.lang.String"));
        assertFalse(delegate.loadCondition("com.hyf.hotrefresh.hello.Test"));
    }
}
