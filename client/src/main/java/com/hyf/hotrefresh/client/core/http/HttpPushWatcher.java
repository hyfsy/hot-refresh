package com.hyf.hotrefresh.client.core.http;

import com.hyf.hotrefresh.client.api.watch.Watcher;
import com.hyf.hotrefresh.common.ChangeType;
import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.args.ArgumentHolder;
import com.hyf.hotrefresh.common.util.ExceptionUtils;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.common.util.UrlUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.hyf.hotrefresh.common.Constants.ARG_SERVER_URL;
import static com.hyf.hotrefresh.common.Constants.REFRESH_API;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
@Deprecated
public class HttpPushWatcher extends Thread implements Watcher {

    private final Map<String, File> changedFileMap = new ConcurrentHashMap<>();

    private String serverAddress;

    private volatile boolean closed = false;

    public HttpPushWatcher() {
        serverAddress = UrlUtils.concat(ArgumentHolder.get(ARG_SERVER_URL), REFRESH_API);
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
        addRequest(file, type);
    }

    @Override
    public void startWatch() {
        start();
    }

    @Override
    public void stopWatch() {
        closed = true;
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

    private void addRequest(File file, ChangeType type) {
        changedFileMap.put(file.getName() + Constants.FILE_NAME_SEPARATOR + type, file);
    }

    private Map<String, File> purge() {
        Map<String, File> fileMap = new LinkedHashMap<>(changedFileMap);
        changedFileMap.clear();
        return fileMap;
    }

    private void push(Map<String, File> fileMap) {
        if (fileMap.isEmpty()) {
            return;
        }

        try (InputStream is = HttpClient.upload(serverAddress, fileMap)) {

            if (is == null) {
                return; // no content
            }

            String content = IOUtils.readAsString(is);
            handleResponseContent(content);
        } catch (IOException e) {
            Log.warn("Upload to " + serverAddress + " failed: " + ExceptionUtils.getNestedMessage(e));
            Log.debug(ExceptionUtils.getStackMessage(e));
        }
    }

    private void handleResponseContent(String content) {
        int idx = content.indexOf(Constants.MESSAGE_SEPARATOR);
        if (idx == -1) {
            Log.info("success");
            return;
        }

        if (Log.isDebugMode()) {
            String debugMessage = content.substring(idx + Constants.MESSAGE_SEPARATOR.length());
            Log.debug(debugMessage);
        }
        else {
            String warnMessage = content.substring(0, idx);
            Log.warn(warnMessage);
            String debugMessage = content.substring(idx + Constants.MESSAGE_SEPARATOR.length());
            Log.debug(debugMessage);
        }
    }
}
