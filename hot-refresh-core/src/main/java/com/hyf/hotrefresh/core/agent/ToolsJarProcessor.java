package com.hyf.hotrefresh.core.agent;

import com.hyf.hotrefresh.common.Log;

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

    public static final String TOOLS_JAR_PATH_PROPERTY = "toolsJarPath";

    private static volatile String toolsJarPath = null;

    public final String toolsJarPathFromSystemProperty = System.getProperty(TOOLS_JAR_PATH_PROPERTY);

    public String getToolsJarPath() {
        return getToolsJarPath(getDefaultFindPath());
    }

    public String getToolsJarPath(String findPath) {

        if (toolsJarPathFromSystemProperty != null) {
            return toolsJarPathFromSystemProperty;
        }

        if (toolsJarPath != null) {
            return toolsJarPath;
        }

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(getToolsJarFindCommand(findPath));
        try {
            Process process = builder.start();
            processResult(process);

            int exitCode = process.waitFor();
            if (exitCode != 0 && Log.isDebugMode()) {
                Log.debug("tools.jar path process return code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            Log.error("Execute find tools.jar process failed", e);
        }
        catch (Throwable t) {
            Log.error("Execute find tools.jar process unknown error", t);
        }

        // make sure not null
        if (toolsJarPath == null) {
            // System.out.println(toolsJarPath);
        }
        return toolsJarPath;
    }

    private void processResult(Process process) {
        Thread successThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), getCharset()))) {
                    toolsJarPath = br.readLine();
                } catch (IOException e) {
                    Log.error("Read find tools.jar process InputStream error", e);
                }
                process.destroy(); // TODO 快速销毁，会导致ErrorStream出错，感觉没有其他办法处理 -> java.io.IOException: Stream closed
            }
        }, "tools.jar-process-input");
        successThread.start();

        Thread errorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), getCharset()))) {
                    String s;
                    while ((s = br.readLine()) != null) {
                        Log.warn(s);
                    }
                } catch (IOException e) {
                    if (Log.isDebugMode()) {
                        Log.error("Read find tools.jar process ErrorStream error", e);
                    }
                }
            }
        }, "tools.jar-process-error");
        errorThread.start();

        Thread stopThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException ignored) {
                }
                if (successThread.isAlive()) {
                    successThread.interrupt();
                }
                if (errorThread.isAlive()) {
                    errorThread.interrupt();
                }
                if (process.isAlive()) {
                    process.destroy();
                }
            }
        }, "tools.jar-process-interrupter");
        stopThread.setDaemon(true);
        stopThread.start();
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
