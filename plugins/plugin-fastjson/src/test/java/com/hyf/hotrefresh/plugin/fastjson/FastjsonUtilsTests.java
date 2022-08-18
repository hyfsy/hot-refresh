package com.hyf.hotrefresh.plugin.fastjson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class FastjsonUtilsTests {

    @Test
    public void testObjectToJson() {
        String s = FastjsonUtils.objectToJson("");
        assertEquals(s, "\"\"");

        s = FastjsonUtils.objectToJson("abc");
        assertEquals(s, "\"abc\"");

        s = FastjsonUtils.objectToJson(null);
        assertEquals(s, "null");

        s = FastjsonUtils.objectToJson(new A());
        assertEquals(s, "{\n\t\"a\":\"b\"\n}");
    }

    public static class A {

        private String a = "b";

        public String getA() {
            return a;
        }
    }
}
