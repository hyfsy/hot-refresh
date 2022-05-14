package com.hyf.hotrefresh.adapter.web;

import com.hyf.hotrefresh.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author baB_hyf
 * @date 2022/05/13
 */
@RunWith(MockitoJUnitRunner.class)
public class HotRefreshFilterTests {

    @Mock
    private HttpServletRequest  request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain         chain;
    @Mock
    private Part                part;

    @Test
    public void testSuccessRefresh() throws Exception {

        assertFalse(Supplier.get());

        when(request.getRequestURI()).thenReturn("/ctxPath/rest/hot-refresh");
        when(request.getContextPath()).thenReturn("/ctxPath");
        when(request.getServletPath()).thenReturn("/rest");
        when(request.getContentType()).thenReturn("multipart/form-data");
        when(request.getParameter("reset")).thenReturn(null);
        when(request.getParts()).thenReturn(Collections.singletonList(part));
        when(part.getInputStream()).thenReturn(getJavaFileInputStream());
        when(part.getName()).thenReturn("Supplier.java" + Constants.FILE_NAME_SEPARATOR + "MODIFY");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        when(response.getWriter()).thenReturn(pw);

        HotRefreshFilter filter = new HotRefreshFilter();
        filter.doFilter(request, response, chain);

        assertEquals(baos.toString(), "");

        assertTrue(Supplier.get());
    }

    private InputStream getJavaFileInputStream() {
        String s = "package com.hyf.hotrefresh.adapter.web;\n" +
                "\n" +
                "/**\n" +
                " * @author baB_hyf\n" +
                " * @date 2022/05/14\n" +
                " */\n" +
                "public class Supplier {\n" +
                "\n" +
                "    public static boolean get() {\n" +
                "        return true;\n" +
                "    }\n" +
                "}";

        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }
}
