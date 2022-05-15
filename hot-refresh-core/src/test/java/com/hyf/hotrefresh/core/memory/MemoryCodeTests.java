package com.hyf.hotrefresh.core.memory;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.util.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class MemoryCodeTests {

    private static final String     fileName = "Test.java";
    private static final String     content  = "aaa";
    private              MemoryCode memoryCode;

    @Before
    public void before() {
        memoryCode = new MemoryCode(fileName, content);
    }

    @Test
    public void testReadAndWrite() throws IOException {

        assertEquals(content, memoryCode.getCharContent(true));
        assertEquals(fileName, memoryCode.getFileName());
        assertEquals(content, memoryCode.getContent());

        try (InputStream is = memoryCode.openInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtils.writeTo(is, baos);
            String s = baos.toString(Constants.MESSAGE_ENCODING.name());
            assertEquals(content, s);
        }

        String newContent = "bbb";

        try (OutputStream os = memoryCode.openOutputStream()) {
            os.write(newContent.getBytes(Constants.MESSAGE_ENCODING));
        }

        assertEquals(newContent, memoryCode.getContent());
        assertEquals(newContent, memoryCode.getCharContent(true));
    }

    @Test
    public void testEquals() {
        MemoryCode origin = new MemoryCode(fileName, "package com.hyf.hotrefresh.test;");
        MemoryCode different = new MemoryCode(fileName, "   package com.hyf.xxx;");
        MemoryCode same = new MemoryCode(fileName, "   package com.hyf.hotrefresh.test;");

        assertNotEquals(origin, different);
        assertEquals(origin, same);
    }
}
