package com.hyf.hotrefresh.agent;

import com.hyf.hotrefresh.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author baB_hyf
 * @date 2021/12/28
 */
public class ToolsJarProcessor {

    public static final String TOOLS_JAR_PATH = System.getProperty("toolsJarPath");

    private static volatile String toolsJarPath = null;

    public String getToolsJarPath() {
        return getToolsJarPath(getDefaultFindPath());
    }

    public String getToolsJarPath(String findPath) {

        if (TOOLS_JAR_PATH != null) {
            return TOOLS_JAR_PATH;
        }

        if (toolsJarPath != null) {
            return toolsJarPath;
        }

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(getToolsJarFindCommand(findPath));
        try {
            Process process = builder.start();
            processResult(process);

            if (process.waitFor() != 0) {
                processError(process);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // make sure not null
        if (toolsJarPath == null) {
            System.out.println(toolsJarPath);
        }
        return toolsJarPath;
    }

    public void processResult(Process process) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), getCharset()))) {
                    toolsJarPath = br.readLine();
                } catch (IOException e) {
                    Log.error("Read find tools.jar process InputStream error", e);
                }
                process.destroy();
            }
        }, "process-input");
        t.start();

        Thread stopProcess = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException ignored) {
                }
                t.interrupt();
                process.destroy();
            }
        }, "process-interrupter");
        stopProcess.setDaemon(true);
        stopProcess.start();
    }

    private void processError(Process process) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"))) {
            String s;
            while ((s = br.readLine()) != null) {
                Log.warn(s);
            }
        } catch (IOException e) {
            Log.error("Read find tools.jar process ErrorStream error", e);
        }
    }

    private String getCharset() {
        return isWindows() ? "GBK" : "UTF-8";
    }

    private String[] getToolsJarFindCommand(String findPath) {
        if (isWindows()) {
            String command = "cmd.exe /c for /r " + findPath + " %i in (tools.jar*) do @echo %i";
            return command.split(" ");
        }
        else {
            String command = "find " + findPath + " -name tools.jar";
            return command.split(" ");
        }
    }

    private String getDefaultFindPath() {
        if (isWindows()) {
            return "C:\\\\";
        }
        else {
            return "/";
        }
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win");
    }
}
