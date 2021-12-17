package com.hyf.hotrefresh.servlet;

import com.hyf.hotrefresh.Constants;
import com.hyf.hotrefresh.HotRefresher;
import com.hyf.hotrefresh.exception.RefreshException;
import com.hyf.hotrefresh.memory.MemoryClassLoader;
import com.hyf.hotrefresh.util.ExceptionUtil;
import com.hyf.hotrefresh.util.IOUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
// @WebFilter("/*")
public class HotRefreshFilter implements Filter {

    private final List<String> blockList = new ArrayList<String>() {{
        // TODO jar内所有类
        add("HotRefreshFilter");
    }};

    public HotRefreshFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // set ctx class loader
        MemoryClassLoader.bind();

        try {
            // match
            if (!uriMatch(req)) {
                chain.doFilter(req, resp);
                return;
            }

            // reset class
            if ("1".equals(req.getParameter("reset"))) {
                try {
                    hotRefreshReset();
                    success(resp);
                } catch (Exception e) {
                    error(resp, e);
                }
                return;
            }

            // contentType match
            String contentType = req.getContentType();
            if (contentType == null || (!contentType.contains("multipart/form-data")
                    && !contentType.contains("multipart/mixed stream"))) {
                success(resp);
                return;
            }

            // parse file content
            Throwable ex = null;
            try {
                Map<String, InputStream> fileStreamMap = getFileStreamMap(req);
                if (fileStreamMap == null || fileStreamMap.isEmpty()) {
                    success(resp);
                    return;
                }

                nextPart:
                for (Map.Entry<String, InputStream> entry : fileStreamMap.entrySet()) {
                    String name = entry.getKey();

                    String[] split = name.split(Constants.FILE_NAME_SEPARATOR);
                    name = split[0];
                    String type = split[1];

                    // only java file
                    if (!name.endsWith(".java")) {
                        continue;
                    }

                    // illegal file
                    for (String s : blockList) {
                        if (name.contains(s)) {
                            break nextPart;
                        }
                    }

                    // hot refresh
                    try (InputStream is = entry.getValue()) {
                        String content = IOUtil.readAsString(is);
                        hotRefresh(name, content, type);
                    } catch (IOException | RefreshException e) {
                        if (ex == null) {
                            ex = e;
                        }
                        else {
                            ex.addSuppressed(ex);
                        }
                    }
                }
            } catch (Throwable e) {
                if (ex == null) {
                    ex = e;
                }
                else {
                    ex.addSuppressed(ex);
                }
            }

            if (ex == null) {
                success(resp);
            }
            else {
                error(resp, ex);
            }
        } finally {
            MemoryClassLoader.unBind();
        }
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

    protected Map<String, InputStream> getFileStreamMap(HttpServletRequest req) throws IOException, ServletException {

        Map<String, InputStream> fileMap = new HashMap<>();

        Collection<Part> parts = req.getParts();

        for (Part part : parts) {
            String name = part.getName();
            fileMap.put(name, part.getInputStream());
        }

        return fileMap;
    }

    private void hotRefresh(String name, String content, String type) throws RefreshException {
        if (content == null || "".equals(content.trim())) {
            throw new RefreshException("No content exist, skip: " + name + " type: " + type);
        }

        HotRefresher.refresh(name, content, type);
    }

    private void hotRefreshReset() throws RefreshException {
        HotRefresher.reset();
    }

    private void success(HttpServletResponse response) {
        response(response, "");
    }

    private void error(HttpServletResponse response, Throwable ex) {
        String sb = ExceptionUtil.getNestedMessage(ex) + Constants.MESSAGE_SEPARATOR + ExceptionUtil.getStackMessage(ex);
        response(response, sb);
    }

    private void response(HttpServletResponse response, String message) {
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/plain;charset=" + Constants.MESSAGE_ENCODING);
            PrintWriter writer = response.getWriter();
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
    }
}
