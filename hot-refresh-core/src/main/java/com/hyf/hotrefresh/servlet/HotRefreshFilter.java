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

        Throwable t = null;
        try {

            // reset class
            if ("1".equals(req.getParameter("reset"))) {
                HotRefresher.reset();
                success(req, resp);
                return;
            }

            // contentType match
            String contentType = req.getContentType();
            if (contentType == null || (!contentType.contains("multipart/form-data")
                    && !contentType.contains("multipart/mixed stream"))) {
                success(req, resp);
                return;
            }

            // parse file content
            Map<String, InputStream> fileStreamMap = getFileStreamMap(req);
            if (fileStreamMap == null || fileStreamMap.isEmpty()) {
                error(req, resp, new RefreshException("No file exists"));
                return;
            }

            nextPart:
            for (Map.Entry<String, InputStream> entry : fileStreamMap.entrySet()) {
                String name = entry.getKey();

                // illegal name
                String[] nameInfo = name.split(Constants.FILE_NAME_SEPARATOR);
                if (nameInfo.length != 2) {
                    continue;
                }

                String fileName = nameInfo[0];
                String type = nameInfo[1];

                // only java file
                if (!fileName.endsWith(".java")) {
                    continue;
                }

                // illegal file
                for (String s : blockList) {
                    if (fileName.contains(s)) {
                        break nextPart;
                    }
                }

                // hot refresh
                try (InputStream is = entry.getValue()) {
                    String content = IOUtil.readAsString(is);
                    if (content != null && !"".equals(content.trim())) {
                        HotRefresher.refresh(fileName, content, type);
                    }
                } catch (IOException | RefreshException e) {
                    if (t == null) {
                        t = e;
                    }
                    else {
                        t.addSuppressed(e);
                    }
                }
            }
        } catch (Throwable e) {
            t = e;
        }

        if (t == null) {
            success(req, resp);
        }
        else {
            error(req, resp, t);
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

    protected void success(HttpServletRequest request, HttpServletResponse response) {
        response(request, response, "");
    }

    protected void error(HttpServletRequest request, HttpServletResponse response, Throwable ex) {
        String sb = ExceptionUtil.getNestedMessage(ex) + Constants.MESSAGE_SEPARATOR + ExceptionUtil.getStackMessage(ex);
        response(request, response, sb);
    }

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
    }
}
