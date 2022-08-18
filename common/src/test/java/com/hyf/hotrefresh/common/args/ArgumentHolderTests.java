package com.hyf.hotrefresh.common.args;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author baB_hyf
 * @date 2022/06/18
 */
public class ArgumentHolderTests {

    @Test
    public void testParse() {

        String[] args = new String[]{"-mock-a", "mockA", "-mock-b", "mockB"};

        assertFalse(ArgumentHolder.get("mock"));

        ArgumentHolder.parse(args);

        assertTrue(ArgumentHolder.get("mock"));
        assertEquals(MockArgumentParser.count, 2);

        args = new String[]{"-mock-a", "mockA"};
        ArgumentHolder.parse(args);
        assertEquals(MockArgumentParser.count, 3);

        assertNull(ArgumentHolder.get("put"));
        ArgumentHolder.put("put", true);
        assertTrue(ArgumentHolder.get("put"));
        ArgumentHolder.put("put", false);
        assertFalse(ArgumentHolder.get("put"));
        ArgumentHolder.remove("put");
        assertNull(ArgumentHolder.get("put"));

    }

    @Argument(value = {"-mock-a", "-mock-b"}, argc = 1)
    public static class MockArgumentParser implements ArgumentParser {

        public static int count = 0;

        @Override
        public void init(Map<String, Object> initArgs) {
            initArgs.put("mock", false);
        }

        @Override
        public void parse(Map<String, Object> parsedArgs, List<String> segments) {
            count++;
            parsedArgs.put("mock", true);
        }
    }
}
