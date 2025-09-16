package com.hyf.hotrefresh.core.refresh;

import com.hyf.hotrefresh.common.util.ReflectionUtils;
import com.hyf.hotrefresh.core.TestJavaFileUtils;
import com.hyf.hotrefresh.core.TestJavaModel;
import com.hyf.hotrefresh.core.exception.AgentException;
import com.hyf.hotrefresh.core.exception.CompileException;
import com.hyf.hotrefresh.core.memory.MemoryClassLoader;
import com.hyf.hotrefresh.core.memory.MemoryCode;
import com.hyf.hotrefresh.core.memory.MemoryCodeCompiler;
import com.hyf.hotrefresh.core.util.Util;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class HotRefreshManagerTests {

    @Test
    public void testGetInstrument() {
        assertNotNull(HotRefreshManager.getInstrumentation());
    }

    @Test
    public void testReTransform() throws CompileException, AgentException {

        assertFalse(invokeMethod(compile(TestJavaFileUtils.getContent())));

        String content = TestJavaFileUtils.getContent();
        content = content.replace("return false", "return true");
        Class<?> clazz = compile(content);

        HotRefreshManager.reTransform(clazz);

        clazz = Util.getThrowawayHotRefreshClassLoader().getClass(TestJavaFileUtils.getClassName());
        assertTrue(invokeMethod(clazz));
        List<Class<?>> classes = Util.getThrowawayHotRefreshClassLoader().clear();

        HotRefreshManager.reTransform(classes.toArray(new Class[0]));

        assertFalse(invokeMethod(compile(TestJavaFileUtils.getContent())));
    }

    private final TestJavaModel javaModel = new TestJavaModel("HotRefreshManagerTests$HotRefreshManagerTests_testReTransformProjectContainsCurrentClass", "com.hyf.hotrefresh.core.refresh");

    @Test
    public void testRemoveAndClearWithinProjectContainsCurrentClass() throws CompileException, AgentException {

        assertFalse(invokeMethod(HotRefreshManagerTests_testReTransformProjectContainsCurrentClass.class));

        compileCurrentClass(javaModel.getReplacedContent());
        HotRefreshManager.reTransform(HotRefreshManagerTests_testReTransformProjectContainsCurrentClass.class);
        assertTrue(invokeMethod(HotRefreshManagerTests_testReTransformProjectContainsCurrentClass.class));
        Class<?> clazz = Util.getThrowawayHotRefreshClassLoader().remove(javaModel.getClassName());
        HotRefreshManager.reTransform(clazz);
        assertFalse(invokeMethod(HotRefreshManagerTests_testReTransformProjectContainsCurrentClass.class));

        compileCurrentClass(javaModel.getReplacedContent());
        HotRefreshManager.reTransform(HotRefreshManagerTests_testReTransformProjectContainsCurrentClass.class);
        assertTrue(invokeMethod(HotRefreshManagerTests_testReTransformProjectContainsCurrentClass.class));
        List<Class<?>> classes = Util.getThrowawayHotRefreshClassLoader().clear();
        HotRefreshManager.reTransform(classes.toArray(new Class[0]));
        assertFalse(invokeMethod(HotRefreshManagerTests_testReTransformProjectContainsCurrentClass.class));
    }

    private Class<?> compile(String content) throws CompileException {
        MemoryClassLoader memoryClassLoader = HotRefreshClassLoader.newInstance();
        MemoryCode memoryCode = new MemoryCode(TestJavaFileUtils.getFileName(), content);
        Map<String, byte[]> compiledBytes = MemoryCodeCompiler.compile(memoryCode);
        memoryClassLoader.store(compiledBytes);
        return memoryClassLoader.getClass(TestJavaFileUtils.getClassName());
    }

    private boolean invokeMethod(Class<?> clazz) {
        Method getMethod = ReflectionUtils.getMethod(clazz, "get");
        return ReflectionUtils.invokeMethod(getMethod, null);
    }

    private Class<?> compileCurrentClass(String content) throws CompileException {
        MemoryClassLoader memoryClassLoader = HotRefreshClassLoader.newInstance();
        MemoryCode memoryCode = new MemoryCode(javaModel.getFileName(), content);
        Map<String, byte[]> compiledBytes = MemoryCodeCompiler.compile(memoryCode);
        memoryClassLoader.store(compiledBytes);
        try {
            return Class.forName(javaModel.getClassName(), false, Util.getOriginContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static class HotRefreshManagerTests_testReTransformProjectContainsCurrentClass {
        public static boolean get() {
            return false;
        }
    }
}
