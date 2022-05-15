package com.hyf.hotrefresh.core.classloader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
@RunWith(MockitoJUnitRunner.class)
public class DelegateClassLoaderTests {

    private static final String CLASS_NAME = "com.hyf.hotrefresh.classloader.Test";

    private ExportLoadConditionDelegateClassLoader delegateClassLoader;
    private ExportLoadConditionDelegateClassLoader conditionTrueDelegateClassLoader;
    private ExportLoadConditionDelegateClassLoader conditionFalseDelegateClassLoader;

    @Mock
    private ClassLoader mockDelegate;

    @Before
    public void before() {
        delegateClassLoader = new ExportLoadConditionDelegateClassLoader(mockDelegate);
        conditionTrueDelegateClassLoader = new ExportLoadConditionDelegateClassLoader(mockDelegate, true);
        conditionFalseDelegateClassLoader = new ExportLoadConditionDelegateClassLoader(mockDelegate, false);
    }

    @Test
    public void testDelegateLoadClass() throws ClassNotFoundException {
        when(mockDelegate.loadClass(CLASS_NAME)).thenReturn(null);
        assertNull(conditionTrueDelegateClassLoader.loadClass(CLASS_NAME));
    }

    @Test(expected = ClassNotFoundException.class)
    public void testDelegateCannotLoadClass() throws ClassNotFoundException {
        when(mockDelegate.loadClass(CLASS_NAME)).thenThrow(new ClassNotFoundException());
        conditionTrueDelegateClassLoader.loadClass(CLASS_NAME);
    }

    @Test
    public void testDelegateLoadClassConditionTrue() throws ClassNotFoundException {
        when(mockDelegate.loadClass(CLASS_NAME)).thenReturn(null);
        assertNull(conditionTrueDelegateClassLoader.loadClass(CLASS_NAME));
    }

    @Test(expected = ClassNotFoundException.class)
    public void testDelegateLoadClassConditionFalse() throws ClassNotFoundException {
        conditionFalseDelegateClassLoader.loadClass(CLASS_NAME);
    }

    private static class ExportLoadConditionDelegateClassLoader extends DelegateClassLoader {

        private boolean condition;

        public ExportLoadConditionDelegateClassLoader(ClassLoader delegate) {
            this(delegate, false);
        }

        public ExportLoadConditionDelegateClassLoader(ClassLoader delegate, boolean condition) {
            super(delegate);
            this.condition = condition;
        }

        @Override
        public boolean loadCondition(String name) {
            return condition;
        }
    }
}
