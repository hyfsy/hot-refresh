package com.hyf.hotrefresh.client;

import com.hyf.hotrefresh.client.http.HttpClient;
import com.hyf.hotrefresh.client.http.HttpPushWatcher;
import com.hyf.hotrefresh.client.watch.WatchCenter;
import com.hyf.hotrefresh.common.Log;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import static com.hyf.hotrefresh.common.Constants.*;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class LocalClient {

    // TODO IDEA plugin
    public static void main(String[] args) {

        // System.setProperty("home", "C:\\Users\\baB_hyf\\Desktop\\test");
        // System.setProperty("server", "http://localhost:8082");

        // java -Dhome=C:\Users\baB_hyf\Desktop\test -Durl=http://localhost:8082 -jar hot-refresh-server-1.0.0-SNAPSHOT.jar

        parse(args);
        print();
        check();
        start();
    }

    private static void parse(String[] args) {
        if (args == null || args.length == 0) {
            return;
        }

        for (int i = 0; i < args.length; i++) {
            if (Arrays.asList("-h", "-H", "--home").contains(args[i])) {
                if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                    continue;
                }
                WATCH_HOME = args[i++ + 1];
            }
            else if (Arrays.asList("-s", "-S", "--server").contains(args[i])) {
                if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                    continue;
                }
                SERVER_URL = args[i++ + 1];
                PUSH_SERVER_URL = SERVER_URL.endsWith("/") ? SERVER_URL.substring(0, SERVER_URL.length() - 1) + REFRESH_API : SERVER_URL + REFRESH_API;
            }
            else if (Arrays.asList("-d", "-D", "--debug").contains(args[i])) {
                DEBUG = true;
            }
        }
    }

    private static void print() {
        String home = "Watch Home Path: " + WATCH_HOME;
        String url = "Refresh Server : " + PUSH_SERVER_URL;

        int max = Math.max(home.getBytes(MESSAGE_ENCODING).length, url.getBytes(MESSAGE_ENCODING).length);
        StringBuilder sb = new StringBuilder(max);
        for (int i = 0; i < max; i++) {
            sb.append('=');
        }

        Log.info("");
        Log.info(sb.toString());
        Log.info(home);
        Log.info(url);
        Log.info(sb.toString());
        Log.info("");
    }

    private static void check() {
        String watchHome = WATCH_HOME;
        File file;
        try {
            file = new File(watchHome);
        } catch (Exception e) {
            throw new RuntimeException("Home path invalid: " + watchHome, e);
        }

        if (!file.isDirectory()) {
            throw new RuntimeException("Home path is not directory");
        }

        Log.info("Wait for connect......");
        try {
            URL url = new URL(PUSH_SERVER_URL);
            url.openConnection();
        } catch (Exception e) {
            throw new RuntimeException("Url invalid: " + PUSH_SERVER_URL, e);
        }

        try {
            HttpClient.getInstance().upload(PUSH_SERVER_URL, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect server: " + PUSH_SERVER_URL, e);
        }

        Log.info("Server connected");
    }

    private static void start() {
        WatchCenter.registerWatcher(WATCH_HOME, new HttpPushWatcher());
    }
}
