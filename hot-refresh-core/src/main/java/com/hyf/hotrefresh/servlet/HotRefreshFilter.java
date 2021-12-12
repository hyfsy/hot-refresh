package com.hyf.hotrefresh.servlet;

import com.hyf.hotrefresh.HotRefresher;
import com.hyf.hotrefresh.Result;
import com.hyf.hotrefresh.Util;
import com.hyf.hotrefresh.exception.RefreshException;
import com.hyf.hotrefresh.memory.MemoryClassLoader;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
@WebFilter("/*")
public class HotRefreshFilter implements Filter {

    public static final String URL = "/hot-refresh";

    public static final String SEPARATOR = "@@@";

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
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        MemoryClassLoader mcl = Util.getThrowawayMemoryClassLoader();
        if (cl != mcl) {
            Thread.currentThread().setContextClassLoader(mcl);
        }

        // match
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        if (!"".equals(contextPath) && !"/".equals(contextPath)) {
            requestURI = requestURI.substring(contextPath.length());
            if (!requestURI.startsWith("/")) {
                requestURI = "/" + requestURI;
            }
            if (requestURI.endsWith("/")) {
                requestURI = requestURI.substring(0, requestURI.length() - 1);
            }
        }
        boolean contains = requestURI.equals(URL);
        if (!contains) {
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

        Exception ex = null;
        try {
            Collection<Part> parts = req.getParts();
            if (parts == null || parts.isEmpty()) {
                success(resp);
                return;
            }

            nextPart:
            for (Part part : parts) {
                String name = part.getName();
                String[] split = name.split(SEPARATOR);
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

                try (InputStream is = part.getInputStream(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                    int len;
                    byte[] bytes = new byte[1024];
                    while ((len = is.read(bytes)) != -1) {
                        baos.write(bytes, 0, len);
                    }

                    String content = baos.toString();
                    hotRefresh(content, name, type);
                } catch (IOException | RefreshException e) {
                    if (ex == null) {
                        ex = e;
                    }
                    else {
                        ex.addSuppressed(ex);
                    }
                }
            }
        } catch (Exception e) {
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
    }

    private void hotRefresh(String content, String name, String type) throws RefreshException {
        if (content == null || "".equals(content.trim())) {
            throw new RefreshException("No content exist, skip: " + name + " type: " + type);
        }

        HotRefresher.refresh(content, name, type);
    }

    private void hotRefreshReset() throws RefreshException {
        HotRefresher.reset();
    }

    private void success(HttpServletResponse response) {
        response(response, Result.success());
    }

    private void error(HttpServletResponse response, Exception ex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ex.printStackTrace(ps);
        response(response, Result.error(baos.toString()));
    }

    private void response(HttpServletResponse response, String message) {
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
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
