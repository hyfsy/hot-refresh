package com.hyf.hotrefresh.client;

import com.hyf.hotrefresh.client.core.rpc.RpcClient;
import com.hyf.hotrefresh.client.plugin.PluginBootstrap;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.Version;
import com.hyf.hotrefresh.common.args.ArgumentHolder;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcHeartbeatRequest;

import java.io.File;
import java.net.URL;

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

        parse(args);
        print();
        check();
        start();
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

        String serverURL = ArgumentHolder.get(ARG_SERVER_URL);
        try {
            URL url = new URL(serverURL);
            url.openConnection();
        } catch (Exception e) {
            throw new RuntimeException("Url invalid: " + serverURL, e);
        }

        try {
            RpcHeartbeatRequest request = new RpcHeartbeatRequest();
            Message message = MessageFactory.createMessage(request);
            RpcClient.getInstance().sync(serverURL, message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect server: " + serverURL, e);
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
