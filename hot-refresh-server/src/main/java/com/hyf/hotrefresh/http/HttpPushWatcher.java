package com.hyf.hotrefresh.http;

import com.hyf.hotrefresh.Constants;
import com.hyf.hotrefresh.log.Log;
import com.hyf.hotrefresh.util.HttpUtil;
import com.hyf.hotrefresh.watch.Watcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class HttpPushWatcher extends Thread implements Watcher {

    public static final String SEPARATOR = "@@@";

    private final Map<String, File> changedFileMap = new ConcurrentHashMap<>();

    private volatile boolean closed = false;

    public HttpPushWatcher() {
        start();
    }

    @Override
    public boolean interest(Object context) {
        if (!(context instanceof Path)) {
            return false;
        }

        Path p = (Path) context;
        return p.toString().endsWith(".java");
    }

    @Override
    public void onChange(File file, Type type) {
        addFile(file, type);
    }

    @Override
    public void stopWatch() {
        closed = true;
    }

    public void push(Map<String, File> fileMap) {
        if (fileMap.isEmpty()) {
            return;
        }

        InputStream is;
        try {
            is = HttpUtil.upload(Constants.PUSH_SERVER_URL, fileMap);
        } catch (IOException e) {
            Log.error("Upload failed: " + Constants.PUSH_SERVER_URL, e);
            return;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            int len;
            byte[] bytes = new byte[1024];
            while ((len = is.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }

            String content = baos.toString("UTF-8");
            handleResponse(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleResponse(String content) {
        int idx = content.indexOf("\r\n");
        if (idx == -1) {
            Log.info("success");
            return;
        }

        String infoMessage = content.substring(0, idx);
        String debugMessage = content.substring(idx + 2);
        if (!Log.isDebugEnabled()) {
            Log.warn(infoMessage);
        }
        Log.debug(debugMessage);
    }

    // TODO name
    private synchronized void addFile(File file, Type type) {
        changedFileMap.put(file.getName() + SEPARATOR + type, file);
    }

    private synchronized Map<String, File> purge() {
        Map<String, File> fileMap = new LinkedHashMap<>(changedFileMap);
        changedFileMap.clear();
        return fileMap;
    }

    @Override
    public void run() {
        while (!closed) {
            push(purge());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
