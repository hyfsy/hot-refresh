package com.hyf.hotrefresh.common.args;

import com.hyf.hotrefresh.common.Constants;
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

        String[] args = new String[]{"-mock-a2", "mockA", "-mock-b2", "mockB"};

        assertFalse(ArgumentHolder.get(MockArgumentParser.class.getName()));

        ArgumentHolder.parse(args);

        assertTrue(ArgumentHolder.get(MockArgumentParser.class.getName()));
    }

    @Argument(value = {"-mock-a2", "-mock-b2"}, argc = 1)
    public static class MockArgumentParser implements ArgumentParser {

        @Override
        public void init(Map<String, Object> initArgs) {
            initArgs.put(MockArgumentParser.class.getName(), false);
        }

        @Override
        public void parse(Map<String, Object> parsedArgs, List<String> segments) {
            parsedArgs.put(MockArgumentParser.class.getName(), true);
        }
    }
}
