package com.hyf.hotrefresh.core.util;

import com.hyf.hotrefresh.core.TestJavaFileUtils;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class ResourceUtilsTests {

    @After
    public void after() throws IOException {
        URL resource = Util.getOriginContextClassLoader().getResource(TestJavaFileUtils.getFileName());
        URL resourceURL = ResourcePersistUtils.getResourceURL(resource);
        Files.delete(new File(resourceURL.getPath()).toPath());
    }

    @Test
    public void testGetResourceURL() throws MalformedURLException {
        URL resource = Util.getOriginContextClassLoader().getResource(TestJavaFileUtils.getFileName());
        URL resourceURL = ResourcePersistUtils.getResourceURL(resource);
        assertNotNull(resourceURL);
        assertEquals("file", resourceURL.getProtocol());
    }
}
