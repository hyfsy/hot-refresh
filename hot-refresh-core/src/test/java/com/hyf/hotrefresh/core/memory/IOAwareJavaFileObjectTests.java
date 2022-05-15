package com.hyf.hotrefresh.core.memory;

import org.junit.Before;
import org.junit.Test;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class IOAwareJavaFileObjectTests {

    private MockIOAwareJavaFileObject mockIOAwareJavaFileObject;

    @Before
    public void before() throws URISyntaxException {
        mockIOAwareJavaFileObject = new MockIOAwareJavaFileObject(new URI("http://www.baidu.com"), JavaFileObject.Kind.SOURCE);
    }

    @Test
    public void testGetInputStream() throws IOException {
        byte[] bytes = {1};

        assertEquals(MockIOAwareJavaFileObject.ORIGIN.length, mockIOAwareJavaFileObject.getSource().length);


        try (InputStream is = mockIOAwareJavaFileObject.openInputStream()) {
            int available = is.available();
            byte[] buf = new byte[available];
            if (is.read(buf) == -1) {
                assertArrayEquals(bytes, buf);
            }
        }

        try (OutputStream os = mockIOAwareJavaFileObject.openOutputStream()) {
            os.write(bytes);
        }

        assertArrayEquals(bytes, mockIOAwareJavaFileObject.getSource());
        assertNotEquals(MockIOAwareJavaFileObject.ORIGIN.length, mockIOAwareJavaFileObject.getSource().length);
        assertEquals(1, mockIOAwareJavaFileObject.getSource().length);

        try (InputStream is = mockIOAwareJavaFileObject.openInputStream()) {
            int available = is.available();
            byte[] buf = new byte[available];
            if (is.read(buf) == -1) {
                assertArrayEquals(bytes, buf);
            }
        }
    }

    public static class MockIOAwareJavaFileObject extends IOAwareJavaFileObject {

        private static final byte[] ORIGIN = new byte[]{1, 2, 3};

        private byte[] source = ORIGIN;

        protected MockIOAwareJavaFileObject(URI uri, Kind kind) {
            super(uri, kind);
        }

        @Override
        protected byte[] inputStreamSource() {
            return source;
        }

        @Override
        protected void outputStreamClosed(byte[] bytes) {
            source = bytes;
        }

        public byte[] getSource() {
            return source;
        }
    }
}
