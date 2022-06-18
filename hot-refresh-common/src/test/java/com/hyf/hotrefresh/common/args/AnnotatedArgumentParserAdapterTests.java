package com.hyf.hotrefresh.common.args;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

/**
 * @author baB_hyf
 * @date 2022/06/18
 */
public class AnnotatedArgumentParserAdapterTests {

    @Test
    public void testDefaultAnnotation() {
        MockArgumentParserNoArgumentAnnotation parser = new MockArgumentParserNoArgumentAnnotation();
        AnnotatedArgumentParserAdapter adapter = new AnnotatedArgumentParserAdapter(parser);
        assertEquals(adapter.argc(), 0);
        assertArrayEquals(adapter.value(), new String[]{});

        Map<String, Object> args = new HashMap<>();

        adapter.init(args);
        assertTrue((boolean) args.get("mock-no-anno-init"));

        adapter.parse(args, null);
        assertTrue((boolean) args.get("mock-no-anno-parse"));
    }

    @Test
    public void testWithAnnotation() {
        MockArgumentParserHasArgumentAnnotation parser = new MockArgumentParserHasArgumentAnnotation();
        AnnotatedArgumentParserAdapter adapter = new AnnotatedArgumentParserAdapter(parser);
        assertEquals(adapter.argc(), 11);
        assertArrayEquals(adapter.value(), new String[]{"-mock-anno-has"});
    }

    @Test
    public void testUseAnnotated() {
        MockAnnotatedArgumentParser parser = new MockAnnotatedArgumentParser();
        AnnotatedArgumentParserAdapter adapter = new AnnotatedArgumentParserAdapter(parser);
        assertEquals(adapter.argc(), MockAnnotatedArgumentParser.MAX_ARGC);
        assertArrayEquals(adapter.value(), new String[]{"-mock-a-a", "-mock-a-b"});
    }

    public static class MockArgumentParserNoArgumentAnnotation implements ArgumentParser {

        @Override
        public void init(Map<String, Object> initArgs) {
            initArgs.put("mock-no-anno-init", true);
        }

        @Override
        public void parse(Map<String, Object> parsedArgs, List<String> segments) {
            parsedArgs.put("mock-no-anno-parse", true);
        }
    }

    @Argument(value = "-mock-anno-has", argc = 11)
    public static class MockArgumentParserHasArgumentAnnotation implements ArgumentParser {

        @Override
        public void parse(Map<String, Object> parsedArgs, List<String> segments) {
        }
    }

    public static class MockAnnotatedArgumentParser implements AnnotatedArgumentParser {

        public static final int MAX_ARGC = 6;

        @Override
        public String[] value() {
            return new String[]{"-mock-a-a", "-mock-a-b"};
        }

        @Override
        public int argc() {
            return MAX_ARGC;
        }

        @Override
        public void parse(Map<String, Object> parsedArgs, List<String> segments) {
        }
    }
}
