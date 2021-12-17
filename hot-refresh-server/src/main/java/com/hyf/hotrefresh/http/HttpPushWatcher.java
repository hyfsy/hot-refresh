package com.hyf.hotrefresh.http;

import com.hyf.hotrefresh.ChangeType;
import com.hyf.hotrefresh.Constants;
import com.hyf.hotrefresh.HttpClient;
import com.hyf.hotrefresh.Log;
import com.hyf.hotrefresh.util.ExceptionUtil;
import com.hyf.hotrefresh.util.IOUtil;
import com.hyf.hotrefresh.watch.Watcher;

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
    public void onChange(File file, ChangeType type) {
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
            is = HttpClient.upload(Constants.PUSH_SERVER_URL, fileMap);
        } catch (IOException e) {
            Log.warn("Upload failed: " + Constants.PUSH_SERVER_URL + Constants.MESSAGE_SEPARATOR + ExceptionUtil.getNestedMessage(e));
            Log.debug(ExceptionUtil.getStackMessage(e));
            return;
        }

        try {
            String content = IOUtil.readAsString(is);
            handleResponseContent(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleResponseContent(String content) {
        int idx = content.indexOf(Constants.MESSAGE_SEPARATOR);
        if (idx == -1) {
            Log.info("success");
            return;
        }

        if (!Log.isDebugMode()) {
            String warnMessage = content.substring(0, idx);
            Log.warn(warnMessage);
        }
        String debugMessage = content.substring(idx + Constants.MESSAGE_SEPARATOR.length());
        Log.debug(debugMessage);
    }

    // TODO name
    private synchronized void addFile(File file, ChangeType type) {
        changedFileMap.put(file.getName() + Constants.FILE_NAME_SEPARATOR + type, file);
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
