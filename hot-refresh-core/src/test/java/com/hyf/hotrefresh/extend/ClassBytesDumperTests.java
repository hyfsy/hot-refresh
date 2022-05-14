package com.hyf.hotrefresh.extend;

import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertTrue;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class ClassBytesDumperTests {

    @Test
    public void testDump() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ClassBytesDumper.dump(ClassBytesDumperTests.class, baos);
        assertTrue(baos.size() != 0);
    }
}
