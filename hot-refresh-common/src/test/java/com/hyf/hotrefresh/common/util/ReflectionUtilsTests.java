package com.hyf.hotrefresh.common.util;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertNotNull;

/**
 * @author baB_hyf
 * @date 2022/05/26
 */
public class ReflectionUtilsTests {

    private final String field = "test_field";

    private boolean get() {
        return false;
    }

    @Test
    public void testSimpleOperation() {

        Method getMethod = ReflectionUtils.getMethod(ReflectionUtilsTests.class, "get");
        assertNotNull(getMethod);
        Field fieldField = ReflectionUtils.getField(ReflectionUtilsTests.class, "field");
        assertNotNull(fieldField);
        assertNotNull(ReflectionUtils.invokeMethod(getMethod, this));
        assertNotNull(ReflectionUtils.invokeFieldGet(fieldField, this));
    }
}
