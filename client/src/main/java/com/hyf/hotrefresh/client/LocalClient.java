package com.hyf.hotrefresh.client;

import com.hyf.hotrefresh.client.core.client.HotRefreshClient;
import com.hyf.hotrefresh.client.plugin.PluginManager;
import com.hyf.hotrefresh.client.watch.HotRefreshWatcher;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.Version;
import com.hyf.hotrefresh.common.args.ArgumentHolder;
import com.hyf.hotrefresh.common.hook.ShutdownHook;

import java.util.concurrent.TimeUnit;

import static com.hyf.hotrefresh.common.Constants.*;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class LocalClient {

    static {
        ShutdownHook.getInstance().addDisposable(LocalClient::exit);
    }

    public static void main(String[] args) {

        // System.setProperty("watchHome", "E:\\study\\code\\idea4\\project\\hot-refresh\\test-cases\\test-spring-boot");
        // System.setProperty("serverURL", "http://localhost:8082");
        // System.setProperty("debug", "1");

        prepare();
        parse(args);
        print();
        start();
    }

    private static void prepare() {
        // for remote debug
        String hotRefreshClientStartWaitSeconds = System.getProperty("hotRefreshClientStartWaitSeconds", "0");
        try {
            TimeUnit.SECONDS.sleep(Integer.parseInt(hotRefreshClientStartWaitSeconds));
        } catch (InterruptedException e) {
            Log.warn("Client start wait is interrupted");
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
        String separator = sb.toString();

        Log.info("");
        Log.info(separator);
        Log.info(home);
        Log.info(url);
        Log.info(version);
        Log.info(separator);
        Log.info("");
    }

    private static void start() {

        Log.info("Waiting to start......");

        HotRefreshWatcher watcher = HotRefreshWatcher.getInstance();
        watcher.startWatch();

        HotRefreshClient client = HotRefreshClient.getInstance();
        client.start();
        client.heartbeat();

        PluginManager.getInstance().install();

        Log.info("Started");
    }

    private static void exit() {

        Log.info("Exiting...");

        PluginManager.getInstance().uninstall();
        HotRefreshClient.getInstance().stop();
        HotRefreshWatcher.getInstance().stopWatch();
    }
}
