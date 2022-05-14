package com.hyf.hotrefresh.classloader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
@RunWith(MockitoJUnitRunner.class)
public class CompositeClassLoaderTests {

    private static final String CLASS_NAME = "com.hyf.hotrefresh.classloader.Test";

    @Mock
    private ExportBrokenLoadClassClassLoader compositeClassLoader;

    @Mock
    private ClassLoader canLoadClassLoader;
    @Mock
    private ClassLoader cannotLoadClassLoader;
    @Mock
    private ClassLoader mockParentClassLoader;

    @Before
    public void before() throws ClassNotFoundException {
        compositeClassLoader = new ExportBrokenLoadClassClassLoader(mockParentClassLoader);
        when(mockParentClassLoader.loadClass(CLASS_NAME)).thenReturn(null);
        when(canLoadClassLoader.loadClass(CLASS_NAME)).thenReturn(null);
        when(cannotLoadClassLoader.loadClass(CLASS_NAME)).thenThrow(new ClassNotFoundException());
    }

    @Test
    public void testAddClassLoader() {
        assertEquals(0, compositeClassLoader.getClassLoaders().size());
        compositeClassLoader.addClassLoader(canLoadClassLoader);
        assertEquals(1, compositeClassLoader.getClassLoaders().size());
    }

    @Test(expected = ClassNotFoundException.class)
    public void testNonClassLoaderLoadThrowException() throws ClassNotFoundException {
        compositeClassLoader.brokenLoadClass(CLASS_NAME);
    }

    @Test
    public void testCanLoadClassLoaderLoadReturnNull() throws ClassNotFoundException {
        compositeClassLoader.addClassLoader(canLoadClassLoader);
        assertNull(compositeClassLoader.brokenLoadClass(CLASS_NAME));
    }

    @Test(expected = ClassNotFoundException.class)
    public void testCannotLoadClassLoaderLoadThrowException() throws ClassNotFoundException {
        compositeClassLoader.addClassLoader(cannotLoadClassLoader);
        compositeClassLoader.brokenLoadClass(CLASS_NAME);
    }

    @Test
    public void testMultiClassLoaderLoadReturnNull() throws ClassNotFoundException {
        compositeClassLoader.addClassLoader(canLoadClassLoader);
        compositeClassLoader.addClassLoader(cannotLoadClassLoader);
        assertNull(compositeClassLoader.brokenLoadClass(CLASS_NAME));
    }

    private static class ExportBrokenLoadClassClassLoader extends CompositeClassLoader {

        public ExportBrokenLoadClassClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        public Class<?> brokenLoadClass(String name) throws ClassNotFoundException {
            return super.brokenLoadClass(name);
        }
    }
}
