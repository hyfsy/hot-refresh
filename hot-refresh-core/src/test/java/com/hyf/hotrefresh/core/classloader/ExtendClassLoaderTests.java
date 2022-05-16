package com.hyf.hotrefresh.core.classloader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
@RunWith(MockitoJUnitRunner.class)
public class ExtendClassLoaderTests {

    private static final String CLASS_NAME = "com.hyf.hotrefresh.classloader.Test";

    @Mock
    private CompositeClassLoader mockCanLoadParentClassLoader;
    @Mock
    private CompositeClassLoader mockCannotLoadParentClassLoader;

    @Before
    public void before() throws ClassNotFoundException {
        when(mockCanLoadParentClassLoader.loadClass(CLASS_NAME)).thenReturn((Class) ExtendClassLoaderTests.class);
        when(mockCannotLoadParentClassLoader.loadClass(CLASS_NAME)).thenThrow(new ClassNotFoundException());
    }

    @Test
    public void testCanLoadClassLoaderLoadReturnClass() throws ClassNotFoundException {
        CanLoadExtendClassLoader canLoadExtendClassLoader = new CanLoadExtendClassLoader(mockCannotLoadParentClassLoader);
        assertEquals(ExtendClassLoaderTests.class, canLoadExtendClassLoader.loadClass(CLASS_NAME));
    }

    @Test(expected = ClassNotFoundException.class)
    public void testCannotLoadClassLoaderLoadReturnNull() throws ClassNotFoundException {
        CanLoadNullExtendClassLoader canLoadNullExtendClassLoader = new CanLoadNullExtendClassLoader(mockCannotLoadParentClassLoader);
        canLoadNullExtendClassLoader.loadClass(CLASS_NAME);
    }

    @Test(expected = ClassNotFoundException.class)
    public void testCannotLoadClassLoaderLoadThrowException() throws ClassNotFoundException {
        CannotLoadAndThrowExceptionExtendClassLoader cannotLoadAndThrowExceptionExtendClassLoader = new CannotLoadAndThrowExceptionExtendClassLoader(mockCannotLoadParentClassLoader);
        cannotLoadAndThrowExceptionExtendClassLoader.loadClass(CLASS_NAME);
    }

    @Test(expected = ClassNotFoundException.class)
    public void testCannotLoadClassLoaderLoadThisFindClass() throws ClassNotFoundException {
        ExtendClassLoader extendClassLoader = new ExtendClassLoader(mockCannotLoadParentClassLoader);
        extendClassLoader.loadClass(CLASS_NAME);
    }

    @Test
    public void testCannotLoadClassLoaderLoadButParentCanLoadReturnNull() throws ClassNotFoundException {
        CanLoadNullExtendClassLoader canLoadNullExtendClassLoader = new CanLoadNullExtendClassLoader(mockCanLoadParentClassLoader);
        assertEquals(ExtendClassLoaderTests.class, canLoadNullExtendClassLoader.loadClass(CLASS_NAME));
    }

    @Test
    public void testCannotLoadClassLoaderLoadButParentCanLoadThrowException() throws ClassNotFoundException {
        CannotLoadAndThrowExceptionExtendClassLoader cannotLoadAndThrowExceptionExtendClassLoader = new CannotLoadAndThrowExceptionExtendClassLoader(mockCanLoadParentClassLoader);
        assertEquals(ExtendClassLoaderTests.class, cannotLoadAndThrowExceptionExtendClassLoader.loadClass(CLASS_NAME));
    }

    @Test
    public void testCannotLoadClassLoaderLoadButParentCanLoadThisFindClass() throws ClassNotFoundException {
        ExtendClassLoader extendClassLoader = new ExtendClassLoader(mockCanLoadParentClassLoader);
        assertEquals(ExtendClassLoaderTests.class, extendClassLoader.loadClass(CLASS_NAME));
    }

    private static class CanLoadExtendClassLoader extends ExtendClassLoader {

        public CanLoadExtendClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Class<?> brokenLoadClass(String name) throws ClassNotFoundException {
            return ExtendClassLoaderTests.class;
        }
    }

    private static class CannotLoadAndThrowExceptionExtendClassLoader extends ExtendClassLoader {

        public CannotLoadAndThrowExceptionExtendClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Class<?> brokenLoadClass(String name) throws ClassNotFoundException {
            throw new ClassNotFoundException();
        }
    }

    private static class CanLoadNullExtendClassLoader extends ExtendClassLoader {

        public CanLoadNullExtendClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Class<?> brokenLoadClass(String name) throws ClassNotFoundException {
            return null;
        }
    }
}