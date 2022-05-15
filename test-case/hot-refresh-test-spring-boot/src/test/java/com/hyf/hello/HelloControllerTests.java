package com.hyf.hello;

import com.hyf.hotrefresh.common.ChangeType;
import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.core.exception.RefreshException;
import com.hyf.hotrefresh.core.memory.MemoryClassLoader;
import com.hyf.hotrefresh.core.refresh.HotRefresher;
import com.hyf.hotrefresh.core.util.Util;
import com.hyf.hotrefresh.hello.controller.HelloController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HelloController.class)
public class HelloControllerTests {

    @Resource
    private HelloController helloController;

    @Test
    public void testLoadOuterClass() throws IOException, RefreshException {
        assertFalse(helloController.loadOuterClass());
        try (InputStream is = Util.getOriginContextClassLoader().getResourceAsStream("Test.java");
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtils.writeTo(is, baos);
            HotRefresher.refresh("Test.java", baos.toString(Constants.MESSAGE_ENCODING.name()), ChangeType.MODIFY.name());
        }
        MemoryClassLoader.bind();
        assertTrue(helloController.loadOuterClass());
        MemoryClassLoader.unBind();
    }

    @Test
    public void testInvokeOuterClass() throws IOException, RefreshException {
        assertFalse(helloController.invokeOuterClass());
        try (InputStream is = Util.getOriginContextClassLoader().getResourceAsStream("Test.java");
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtils.writeTo(is, baos);
            HotRefresher.refresh("Test.java", baos.toString(Constants.MESSAGE_ENCODING.name()), ChangeType.MODIFY.name());
        }
        MemoryClassLoader.bind();
        assertTrue(helloController.invokeOuterClass());
        MemoryClassLoader.unBind();
    }

    @Test
    public void testModifySelf() throws RefreshException {
        assertFalse(helloController.modifySelf());

        String content = "package com.hyf.hotrefresh.hello.controller;\n" +
                "\n" +
                "import org.springframework.context.ApplicationContext;\n" +
                "import org.springframework.util.ClassUtils;\n" +
                "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                "import org.springframework.web.bind.annotation.RestController;\n" +
                "\n" +
                "import javax.annotation.Resource;\n" +
                "import java.lang.reflect.Method;\n" +
                "\n" +
                "/**\n" +
                " * @author baB_hyf\n" +
                " * @date 2021/12/12\n" +
                " */\n" +
                "@RestController\n" +
                "@RequestMapping(\"test\")\n" +
                "public class HelloController {\n" +
                "\n" +
                "    @Resource\n" +
                "    private ApplicationContext context;\n" +
                "\n" +
                "    @RequestMapping(\"1\")\n" +
                "    public boolean loadOuterClass() {\n" +
                "        try {\n" +
                "            // see resource directory\n" +
                "            ClassUtils.forName(\"com.hyf.hotrefresh.hello.Test\", null);\n" +
                "            return true;\n" +
                "        } catch (Throwable e) {\n" +
                "        }\n" +
                "\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "    @RequestMapping(\"2\")\n" +
                "    public boolean invokeOuterClass() {\n" +
                "        try {\n" +
                "            Class<?> clazz = ClassUtils.forName(\"com.hyf.hotrefresh.hello.Test\", null);\n" +
                "            Method getMethod = clazz.getMethod(\"get\");\n" +
                "            return (boolean) getMethod.invoke(null);\n" +
                "        } catch (Throwable e) {\n" +
                "        }\n" +
                "\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "    @RequestMapping(\"3\")\n" +
                "    public boolean modifySelf() {\n" +
                "        return false;\n" +
                "    }\n" +
                "\n" +
                "    @RequestMapping(\"4\")\n" +
                "    public String compileParameters(String param) {\n" +
                "        return \"4\";\n" +
                "    }\n" +
                "}\n";

        content = content.replace(
                "    @RequestMapping(\"3\")\n" +
                        "    public boolean modifySelf() {\n" +
                        "        return false;\n" +
                        "    }",
                "    @RequestMapping(\"3\")\n" +
                        "    public boolean modifySelf() {\n" +
                        "        return true;\n" +
                        "    }");

        HotRefresher.refresh("HelloController.java", content, ChangeType.MODIFY.name());

        assertTrue(helloController.modifySelf());
    }

    @Test
    public void testCompileParameters() throws NoSuchMethodException {
        DefaultParameterNameDiscoverer defaultParameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        Method compileParametersMethod = helloController.getClass().getMethod("compileParameters", String.class);
        String[] parameterNames = defaultParameterNameDiscoverer.getParameterNames(compileParametersMethod);
        assertArrayEquals(parameterNames, new String[]{"param"});
    }
}
