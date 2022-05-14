package com.hyf.hotrefresh.util;

import com.hyf.hotrefresh.TestJavaFileUtils;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class ResourceUtilTests {

    @Test
    public void testGetResourceURL() throws MalformedURLException {
        URL resource = Util.getOriginContextClassLoader().getResource(TestJavaFileUtils.getFileName());
        URL resourceURL = ResourceUtil.getResourceURL(resource);
        assertNotNull(resourceURL);
        assertEquals("file", resourceURL.getProtocol());
    }
}
