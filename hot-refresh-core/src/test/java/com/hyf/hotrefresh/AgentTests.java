package com.hyf.hotrefresh;

import com.hyf.hotrefresh.exception.RefreshException;
import com.hyf.hotrefresh.memory.MemoryCode;
import com.hyf.hotrefresh.memory.MemoryCodeCompiler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
    public void testDownload() {
        Util.getInfrastructureJarClassLoader();
    }

    @Test
    public void testObfuscation() throws RefreshException {
        // Map<String, byte[]> aaa = new HashMap<>();
        // Map<String, byte[]> obfuscation = HotRefresher.obfuscation(aaa);
        // System.out.println(obfuscation == aaa);
    }

    @Test
    public void testRefresh() throws RefreshException {
        String fileName = "Test.java";
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

        com.hyf.hotrefresh.Test.main(null);
        HotRefresher.refresh(fileName, content, "MODIFY");
        com.hyf.hotrefresh.Test.main(null);
    }

    @Test
    public void testCompile() {
        String fileName = "Test2.java";
        String content = "package com.hyf.hotrefresh;\n" +
                "\n" +
                "/**\n" +
                " * @author baB_hyf\n" +
                " * @date 2021/12/11\n" +
                " */\n" +
                "public class Test2\n" +
                "{\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(2);\n" +
                "    }\n" +
                "}\n";

        try {
            Map<String, byte[]> compile = MemoryCodeCompiler.compile(new MemoryCode(fileName, content));

            for (Map.Entry<String, byte[]> entry : compile.entrySet()) {
                String className = entry.getKey();
                byte[] bytes = entry.getValue();

                Util.getThrowawayMemoryClassLoader().store(compile);
                Class<?> clazz = Class.forName(className, false, Util.getThrowawayMemoryClassLoader());
                System.out.println(clazz);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
