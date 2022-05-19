package com.hyf.hotrefresh.adapter.web;

import com.hyf.hotrefresh.core.remoting.payload.RpcHotRefreshRequest;
import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcResponse;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageEncoding;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcRequestInst;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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

    @Test
    public void testSuccessRefresh() throws Exception {

        assertFalse(Supplier.get());

        RpcHotRefreshRequest request = new RpcHotRefreshRequest();
        request.setFileName("Supplier.java");
        request.setFileLocation(null);
        request.setInst(RpcRequestInst.MODIFY);
        request.setContent(getJavaFileInputStream());
        Message message = MessageFactory.createMessage(request);
        byte[] encode = MessageCodec.encode(message);
        ByteArrayServletInputStream basis = new ByteArrayServletInputStream(new ByteArrayInputStream(encode));

        when(this.request.getRequestURI()).thenReturn("/ctxPath/rest/hot-refresh");
        when(this.request.getContextPath()).thenReturn("/ctxPath");
        when(this.request.getServletPath()).thenReturn("/rest");
        when(this.request.getContentType()).thenReturn(RemotingConstants.DEFAULT_CONTENT_TYPE);
        when(this.request.getParameter("reset")).thenReturn(null);
        when(this.request.getInputStream()).thenReturn(basis);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayServletOutputStream basos = new ByteArrayServletOutputStream(baos);
        when(response.getOutputStream()).thenReturn(basos);

        HotRefreshFilter filter = new HotRefreshFilter();
        filter.doFilter(this.request, response, chain);

        byte[] bytes = baos.toByteArray();
        Message response = MessageCodec.decode(bytes);
        RpcResponse rpcResponse = (RpcResponse) response.getBody();

        String msg = new String(rpcResponse.getData(), RpcMessageEncoding.getEncoding(response.getEncoding()).getCharset());
        assertEquals(msg, "");

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

    public static class ByteArrayServletInputStream extends ServletInputStream {

        private ByteArrayInputStream bais;

        public ByteArrayServletInputStream(ByteArrayInputStream bais) {
            this.bais = bais;
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }

        @Override
        public int read() throws IOException {
            return bais.read();
        }
    }

    public static class ByteArrayServletOutputStream extends ServletOutputStream {

        private ByteArrayOutputStream baos;

        public ByteArrayServletOutputStream(ByteArrayOutputStream baos) {
            this.baos = baos;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }

        @Override
        public void write(int b) throws IOException {
            baos.write(b);
        }
    }
}
