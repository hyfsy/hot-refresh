package com.hyf.hotrefresh.plugin.web;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.util.ExceptionUtils;
import com.hyf.hotrefresh.core.exception.RefreshException;
import com.hyf.hotrefresh.core.memory.MemoryClassLoader;
import com.hyf.hotrefresh.core.refresh.HotRefresher;
import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import com.hyf.hotrefresh.remoting.exception.ServerException;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcErrorResponse;
import com.hyf.hotrefresh.remoting.server.HotRefreshServer;
import com.hyf.hotrefresh.remoting.server.RpcServer;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
@WebFilter("/*")
public class HotRefreshFilter implements Filter {

    private final List<String> blockList = new ArrayList<String>() {{
        // TODO jar内所有类
        add("HotRefreshFilter");
    }};

    private final RpcServer server = new HotRefreshServer();

    private Exception serverException = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String blocks = filterConfig.getInitParameter("blocks");
        if (blocks != null) {
            blockList.addAll(Arrays.asList(blocks.split(",")));
        }
        try {
            server.start();
        } catch (ServerException e) {
            // hot refresh server start failed but cannot effect the environment
            // and we also need to know why we failed
            serverException = e;
            Log.error("Server start failed", e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // uri match
        if (!uriMatch(req)) {
            // bind ccl
            MemoryClassLoader.bind();
            try {
                chain.doFilter(req, resp);
            } finally {
                MemoryClassLoader.unBind();
            }
            return;
        }

        // reset class
        if ("1".equals(req.getParameter("reset"))) {
            try {
                HotRefresher.reset();
                success(req, resp);
            } catch (RefreshException e) {
                if (Log.isDebugMode()) {
                    Log.error("Reset class failed", e);
                }
            }
            return;
        }

        // contentType match
        String contentType = req.getContentType();
        if (contentType == null || (!contentType.equals(RemotingConstants.DEFAULT_CONTENT_TYPE))) {
            success(req, resp);
            return;
        }

        // server start failed
        if (serverException != null) {
            ServletOutputStream os = resp.getOutputStream();
            RpcErrorResponse rpcErrorResponse = new RpcErrorResponse();
            rpcErrorResponse.setThrowable(serverException);
            os.write(MessageCodec.encode(MessageFactory.createMessage(rpcErrorResponse)));
            os.flush();
        }

        server.handle(req.getInputStream(), resp.getOutputStream());
    }

    protected boolean uriMatch(HttpServletRequest req) {
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();

        if (requestURI.endsWith("/")) {
            requestURI = requestURI.substring(0, requestURI.length() - 1);
        }

        if ("/".equals(contextPath)) {
            contextPath = "";
        }
        if (contextPath.endsWith("/")) {
            contextPath = contextPath.substring(0, contextPath.length() - 1);
        }

        if ("/".equals(servletPath)) {
            servletPath = "";
        }
        if (servletPath.endsWith("/")) {
            servletPath = servletPath.substring(0, servletPath.length() - 1);
        }
        if (servletPath.equals(Constants.REFRESH_API)) {
            servletPath = "";
        }

        String requestPath = contextPath + servletPath + Constants.REFRESH_API;
        return requestURI.equals(requestPath);
    }

    /**
     * @deprecated recommend to use rpc style response to return
     */
    protected void success(HttpServletRequest request, HttpServletResponse response) {
        response(request, response, "");
    }

    /**
     * @deprecated recommend to use rpc style response to return
     */
    protected void error(HttpServletRequest request, HttpServletResponse response, Throwable ex) {
        String sb = ExceptionUtils.getNestedMessage(ex) + Constants.MESSAGE_SEPARATOR + ExceptionUtils.getStackMessage(ex);
        response(request, response, sb);
    }

    /**
     * @deprecated recommend to use rpc style response to return
     */
    private void response(HttpServletRequest request, HttpServletResponse response, String message) {
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/plain;charset=" + Constants.MESSAGE_ENCODING.name());
            PrintWriter writer = response.getWriter();
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        try {
            server.stop();
        } catch (ServerException e) {
            Log.error("Server stop failed", e);
        }
    }
}
