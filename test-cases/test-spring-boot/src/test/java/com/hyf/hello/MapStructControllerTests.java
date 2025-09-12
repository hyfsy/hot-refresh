package com.hyf.hello;

import com.hyf.hotrefresh.common.ChangeType;
import com.hyf.hotrefresh.core.exception.RefreshException;
import com.hyf.hotrefresh.core.refresh.HotRefresher;
import com.hyf.hotrefresh.hello.controller.MapStructController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MapStructController.class)
public class MapStructControllerTests {

    @Resource
    private MapStructController mapStrutController;

    @Test
    public void testAddCompiledMethod() throws RefreshException {
        assertFalse(mapStrutController.addCompiledMethod());

        String content = "package com.hyf.hotrefresh.hello.convert;\n" +
                "\n" +
                "import com.hyf.hotrefresh.hello.entity.PersonDo;\n" +
                "import com.hyf.hotrefresh.hello.entity.PersonDto;\n" +
                "import org.mapstruct.Mapper;\n" +
                "import org.mapstruct.factory.Mappers;\n" +
                "\n" +
                "/**\n" +
                " * @author baB_hyf\n" +
                " * @date 2022/05/14\n" +
                " */\n" +
                "@Mapper\n" +
                "public interface HelloConverter {\n" +
                "\n" +
                "    HelloConverter INSTANCE = Mappers.getMapper(HelloConverter.class);\n" +
                "\n" +
                "    // PersonDto convert(PersonDo personDo);\n" +
                "}\n";

        content = content.replace("// PersonDto convert(PersonDo personDo);", "PersonDto convert(PersonDo personDo);");

        HotRefresher.refresh("HelloConverter.java", content.getBytes(StandardCharsets.UTF_8), ChangeType.MODIFY.name());

        assertTrue(mapStrutController.addCompiledMethod());
    }
}
