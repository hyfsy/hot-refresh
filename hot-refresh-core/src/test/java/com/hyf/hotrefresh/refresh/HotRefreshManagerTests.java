package com.hyf.hotrefresh.refresh;

import com.hyf.hotrefresh.TestJavaFileUtils;
import com.hyf.hotrefresh.exception.AgentException;
import com.hyf.hotrefresh.exception.CompileException;
import com.hyf.hotrefresh.memory.MemoryClassLoader;
import com.hyf.hotrefresh.memory.MemoryCode;
import com.hyf.hotrefresh.memory.MemoryCodeCompiler;
import com.hyf.hotrefresh.util.InfrastructureJarClassLoader;
import org.junit.Test;

import java.lang.reflect.Method;
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

        clazz = MemoryClassLoader.newInstance().getClass(TestJavaFileUtils.getClassName());
        assertTrue(invokeMethod(clazz));
    }

    private Class<?> compile(String content) throws CompileException {
        MemoryClassLoader memoryClassLoader = MemoryClassLoader.newInstance();
        MemoryCode memoryCode = new MemoryCode(TestJavaFileUtils.getFileName(), content);
        Map<String, byte[]> compiledBytes = MemoryCodeCompiler.compile(memoryCode);
        memoryClassLoader.store(compiledBytes);
        return memoryClassLoader.getClass(TestJavaFileUtils.getClassName());
    }

    private boolean invokeMethod(Class<?> clazz) {
        Method getMethod = InfrastructureJarClassLoader.getInstance().getMethod(clazz, "get");
        return InfrastructureJarClassLoader.getInstance().invokeMethod(getMethod, null);
    }
}
