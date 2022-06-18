package com.hyf.hotrefresh.common.args;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author baB_hyf
 * @date 2022/06/18
 */
public class ArgumentParserTests {

    @Test
    public void testParserDefaultAnnotation() {

        String[] args = new String[]{"-mock-a", "mockA", "-mock-b", "mockB"};

        assertFalse(ArgumentHolder.get("mock"));

        ArgumentHolder.parse(args);

        assertTrue(ArgumentHolder.get("mock"));
    }

    @Argument(value = {"-mock-a", "-mock-b"}, argc = 1)
    public static class MockArgumentParser implements ArgumentParser {

        @Override
        public void init(Map<String, Object> initArgs) {
            initArgs.put("mock", false);
        }

        @Override
        public void parse(Map<String, Object> parsedArgs, List<String> segments) {
            parsedArgs.put("mock", true);
        }
    }
}
