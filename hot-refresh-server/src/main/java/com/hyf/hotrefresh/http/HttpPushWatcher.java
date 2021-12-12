package com.hyf.hotrefresh.http;

import com.hyf.hotrefresh.Constants;
import com.hyf.hotrefresh.util.HttpUtil;
import com.hyf.hotrefresh.watch.Watcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class HttpPushWatcher implements Watcher {

    public static final String SEPARATOR = "@@@";

    private final Map<String, File> changedFileMap = new LinkedHashMap<>();

    // TODO distinct
    @Override
    public void onChange(File file, Type type) {
        addFile(file, type);
        push(purge());
    }

    public void push(Map<String, File> fileMap) {
        if (fileMap.isEmpty()) {
            return;
        }

        InputStream is;
        try {
            is = HttpUtil.upload(Constants.PUSH_SERVER_URL, fileMap);
        } catch (IOException e) {
            System.out.println("Upload failed: " + e.getMessage());
            // e.printStackTrace();
            return;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            int len;
            byte[] bytes = new byte[1024];
            while ((len = is.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }

            String content = baos.toString();
            System.out.println(content);

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

    // TODO name
    private synchronized void addFile(File file, Type type) {
        changedFileMap.put(file.getName() + SEPARATOR + type, file);
    }

    private synchronized Map<String, File> purge() {
        Map<String, File> fileMap = new LinkedHashMap<>(changedFileMap);
        changedFileMap.clear();
        return fileMap;
    }
}
