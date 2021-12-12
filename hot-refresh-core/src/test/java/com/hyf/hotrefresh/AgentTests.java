package com.hyf.hotrefresh;

import com.hyf.hotrefresh.exception.RefreshException;
import com.hyf.hotrefresh.memory.MemoryClassLoader;
import com.hyf.hotrefresh.memory.MemoryCompiler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class AgentTests {

    @Test
    public void testInstall() {
        Assertions.assertNotNull(HotRefreshManager.getInstrumentation());
    }

    @Test
    public void testRefresh() throws RefreshException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String simpleClassName = "Test";
        String content = "package com.hyf.hotrefresh;\n" +
                "\n" +
                "/**\n" +
                " * @author baB_hyf\n" +
                " * @date 2021/12/11\n" +
                " */\n" +
                "public class Test\n" +
                "{\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(2);\n" +
                "    }\n" +
                "}\n";

        // com.hyf.hotrefresh.Test.main(null);
        HotRefresher.refresh(content, simpleClassName, "CREATE");
        // com.hyf.hotrefresh.Test.main(null);
    }

    @Test
    public void testCompile() {
        String simpleClassName = "Test";
        String content = "package com.hyf.hotrefresh;\n" +
                "\n" +
                "/**\n" +
                " * @author baB_hyf\n" +
                " * @date 2021/12/11\n" +
                " */\n" +
                "public class Test\n" +
                "{\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(1);\n" +
                "    }\n" +
                "}\n";

        try {
            Map<String, byte[]> compile = MemoryCompiler.compile(simpleClassName, content);

            for (Map.Entry<String, byte[]> entry : compile.entrySet()) {
                String key = entry.getKey();
                byte[] value = entry.getValue();

                String className = MemoryClassLoader.newInstance().store(simpleClassName, value);
                Class<?> clazz = Class.forName(className, false, MemoryClassLoader.newInstance());
                System.out.println(clazz);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
