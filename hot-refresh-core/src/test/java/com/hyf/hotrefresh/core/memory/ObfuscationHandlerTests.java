package com.hyf.hotrefresh.core.memory;

import com.hyf.hotrefresh.core.TestJavaFileUtils;
import com.hyf.hotrefresh.core.exception.CompileException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class ObfuscationHandlerTests {

    public static final String CLASS_NAME = "com.hyf.hotrefresh.test.obfuscation.Test";

    private static byte[] bytes;

    @Before
    public void before() throws CompileException {
        String content = TestJavaFileUtils.getContent();
        content = content.replace("package com.hyf.hotrefresh", "package com.hyf.hotrefresh.test.obfuscation");

        MemoryCode memoryCode = new MemoryCode(TestJavaFileUtils.getFileName(), content);
        bytes = MemoryCodeCompiler.compile(memoryCode).get(CLASS_NAME);
    }

    @After
    public void after() {
        bytes = null;
    }

    @Test
    public void testObfuscationHandle() throws CompileException {
        String content = TestJavaFileUtils.getContent();
        MemoryCode memoryCode = new MemoryCode("Test.java", content);
        Map<String, byte[]> compiledBytes = MemoryCodeCompiler.compile(memoryCode);
        assertNotNull(compiledBytes.get(CLASS_NAME));
    }

    public static class MockObfuscationHandler implements ObfuscationHandler {

        @Override
        public Map<String, byte[]> handle(Map<String, byte[]> compiledBytes) {
            if (bytes != null) {
                compiledBytes.put(CLASS_NAME, bytes);
            }
            return compiledBytes;
        }
    }
}
