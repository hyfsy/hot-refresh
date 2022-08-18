package com.hyf.hello;

import com.hyf.hotrefresh.common.ChangeType;
import com.hyf.hotrefresh.core.exception.RefreshException;
import com.hyf.hotrefresh.core.refresh.HotRefresher;
import com.hyf.hotrefresh.hello.controller.LombokController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LombokController.class)
public class LombokControllerTests {

    @Resource
    private LombokController lombokController;

    @Test
    public void testHasLogField() throws RefreshException {
        assertFalse(lombokController.hasLogField());

        String content = "package com.hyf.hotrefresh.hello.controller;\n" +
                "\n" +
                "import lombok.extern.slf4j.Slf4j;\n" +
                "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                "import org.springframework.web.bind.annotation.RestController;\n" +
                "\n" +
                "/**\n" +
                " * @author baB_hyf\n" +
                " * @date 2022/05/13\n" +
                " */\n" +
                "// @Slf4j\n" +
                "@RestController\n" +
                "@RequestMapping(\"test/lombok\")\n" +
                "public class LombokController {\n" +
                "\n" +
                "    @RequestMapping(\"1\")\n" +
                "    public boolean hasLogField() {\n" +
                "        try {\n" +
                "            LombokController.class.getDeclaredField(\"log\");\n" +
                "            return true;\n" +
                "        } catch (Throwable e) {\n" +
                "        }\n" +
                "        return false;\n" +
                "    }\n" +
                "}";

        content = content.replace("// @Slf4j", "@Slf4j");

        HotRefresher.refresh("LombokController.java", content, ChangeType.MODIFY.name());

        assertTrue(lombokController.hasLogField());
    }
}
