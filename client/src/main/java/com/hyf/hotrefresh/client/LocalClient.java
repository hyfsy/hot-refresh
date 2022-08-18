package com.hyf.hotrefresh.client;

import com.hyf.hotrefresh.client.api.plugin.PluginBootstrap;
import com.hyf.hotrefresh.client.core.client.HotRefreshClient;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.Version;
import com.hyf.hotrefresh.common.args.ArgumentHolder;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcHeartbeatRequest;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static com.hyf.hotrefresh.common.Constants.*;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class LocalClient {

    // TODO IDEA plugin
    public static void main(String[] args) {

        // System.setProperty("watchHome", "E:\\study\\code\\idea4\\project\\hot-refresh\\test-cases\\hot-refresh-test-spring-boot");
        // System.setProperty("serverURL", "http://localhost:8082");
        // System.setProperty("debug", "1");

        prepare();
        parse(args);
        print();
        check();
        start();
    }

    private static void prepare() {
        String hotRefreshClientStartWaitSeconds = System.getProperty("hotRefreshClientStartWaitSeconds", "0");
        try {
            TimeUnit.SECONDS.sleep(Integer.parseInt(hotRefreshClientStartWaitSeconds));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void parse(String[] args) {
        ArgumentHolder.parse(args);
    }

    private static void print() {
        String home = "Watch Home Path: " + ArgumentHolder.get(ARG_WATCH_HOME);
        String url = "Refresh Server : " + ArgumentHolder.get(ARG_SERVER_URL);
        String version = "Client Version : " + Version.getVersion();

        int max = Math.max(home.getBytes(MESSAGE_ENCODING).length, Math.max(url.getBytes(MESSAGE_ENCODING).length, version.getBytes(MESSAGE_ENCODING).length));
        StringBuilder sb = new StringBuilder(max);
        for (int i = 0; i < max; i++) {
            sb.append('=');
        }

        Log.info("");
        Log.info(sb.toString());
        Log.info(home);
        Log.info(url);
        Log.info(version);
        if (Log.isDebugMode()) {
            Log.info("Debug Arguments: " + ArgumentHolder.getMap());
        }
        Log.info(sb.toString());
        Log.info("");
    }

    private static void check() {

        String watchHome = ArgumentHolder.get(ARG_WATCH_HOME);

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

        String serverAddress = ArgumentHolder.get(ARG_SERVER_URL);
        try {
            RpcHeartbeatRequest request = new RpcHeartbeatRequest();
            HotRefreshClient.getInstance().sendRequest(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect server: " + serverAddress, e);
        }

        Log.info("Server connected");
    }

    private static void start() {
        setupPlugin();
    }

    private static void setupPlugin() {
        new PluginBootstrap().boot();
    }
}
