package com.hyf.hotrefresh;

import com.hyf.hotrefresh.http.HttpPushWatcher;
import com.hyf.hotrefresh.watch.WatchCenter;

import java.io.File;
import java.net.URL;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class LocalServer {

    public static void main(String[] args) {

        // System.setProperty("home", "C:\\Users\\baB_hyf\\Desktop\\test");
        // System.setProperty("url", "http://localhost:8082");

        // java -Dhome=C:\Users\baB_hyf\Desktop\test -Durl=http://localhost:8082 -jar hot-refresh-server-1.0.0-SNAPSHOT.jar

        printInfo();
        check();

        WatchCenter.registerWatcher(Constants.WATCH_HOME, new HttpPushWatcher());
    }

    private static void printInfo() {
        String home = "Watch Home Path: " + Constants.WATCH_HOME;
        String url = "Refresh Server : " + Constants.PUSH_SERVER_URL;

        int max = Math.max(home.length(), url.length());
        StringBuilder sb = new StringBuilder(max);
        for (int i = 0; i < max; i++) {
            sb.append('=');
        }

        System.out.println();
        System.out.println(sb.toString());
        System.out.println(home);
        System.out.println(url);
        System.out.println(sb.toString());
        System.out.println();
    }

    private static void check() {
        String watchHome = Constants.WATCH_HOME;
        File file;
        try {
            file = new File(watchHome);
        } catch (Exception e) {
            throw new RuntimeException("Home invalid: " + watchHome, e);
        }

        if (!file.isDirectory()) {
            throw new RuntimeException("Home is not directory");
        }

        System.out.println("Wait for connect......");
        try {
            URL url = new URL(Constants.PUSH_SERVER_URL);
            url.openConnection();
        } catch (Exception e) {
            throw new RuntimeException("Url invalid: " + Constants.PUSH_SERVER_URL, e);
        }

        try {
            HttpClient.upload(Constants.PUSH_SERVER_URL, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect server: " + Constants.PUSH_SERVER_URL);
        }

        System.out.println("Server connected");
    }
}
