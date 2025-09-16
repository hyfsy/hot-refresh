package com.hyf.hotrefresh.core;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class TestJavaModelTests {

    @Test
    public void testGetClassBytes() throws Exception {
        TestJavaModel testJavaModel = new TestJavaModel();
        byte[] classBytes = testJavaModel.getClassBytes();
        // IOUtils.writeTo(new ByteArrayInputStream(classBytes), new FileOutputStream("E:\\" + testJavaModel.getClassFileName()));
        assertNotNull(classBytes);
    }
}
