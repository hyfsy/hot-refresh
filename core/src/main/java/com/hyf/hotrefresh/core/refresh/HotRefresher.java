package com.hyf.hotrefresh.core.refresh;

import com.hyf.hotrefresh.common.ChangeType;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.core.event.ByteCodeRefreshedEvent;
import com.hyf.hotrefresh.core.event.HotRefreshEventPublisher;
import com.hyf.hotrefresh.core.exception.RefreshException;
import com.hyf.hotrefresh.core.install.CoreInstaller;
import com.hyf.hotrefresh.core.memory.MemoryCode;
import com.hyf.hotrefresh.core.memory.MemoryCodeCompiler;
import com.hyf.hotrefresh.core.util.InfraUtils;
import com.hyf.hotrefresh.core.util.Util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class HotRefresher {

    public static void refresh(String fileName, byte[] fileContent, String fileChangeType) throws RefreshException {

        if (!CoreInstaller.enable()) {
            return;
        }

        try {

            Map<String, byte[]> classBytesMap = new LinkedHashMap<>();

            // java文件
            if (fileName.endsWith(".java")) {
                Map<String, byte[]> compiledBytes = classBytesMap = MemoryCodeCompiler.compile(new MemoryCode(fileName, new String(fileContent)), Util.getThrowawayHotRefreshClassLoader());
                if (compiledBytes == null || compiledBytes.isEmpty()) {
                    if (Log.isDebugMode()) {
                        Log.info("Non class compiled: " + fileName);
                    }
                    return;
                }
                classBytesMap.putAll(compiledBytes);
            }
            // class文件
            else if (fileName.endsWith(".class")) {
                String className = InfraUtils.getClassName(fileContent);
                classBytesMap.put(className, fileContent);
            } else {
                // file suffix not supported
                return;
            }

            Util.getThrowawayHotRefreshClassLoader().store(classBytesMap);

            for (Map.Entry<String, byte[]> entry : classBytesMap.entrySet()) {
                String className = entry.getKey();

                ChangeType changeType = ChangeType.valueOf(fileChangeType);

                // loaded
                if (ChangeType.CREATE == changeType) {
                }
                // transform
                else if (ChangeType.MODIFY == changeType) {
                    try {
                        Class<?> clazz = Class.forName(className, false, Util.getOriginContextClassLoader());
                        HotRefreshManager.reTransform(clazz);
                    } catch (ClassNotFoundException ignored) {
                        // prevent modify event but class not exist
                        Class.forName(className, false, Util.getThrowawayHotRefreshClassLoader());
                    }
                }
                // unload
                else if (ChangeType.DELETE == changeType) {
                    reset(className);
                }
            }

            HotRefreshEventPublisher eventPublisher = HotRefreshEventPublisher.getInstance();
            eventPublisher.publishEvent(new ByteCodeRefreshedEvent(classBytesMap));
        } catch (RefreshException e) {
            throw e;
        } catch (Throwable e) {
            throw new RefreshException("Hot refresh failed", e);
        }
    }

    public static void start() {
        HotRefreshManager.start();
    }

    public static void stop() {
        HotRefreshManager.stop();
    }

    /**
     * @deprecated refreshed class has been loaded by app class loader and delete the file cannot
     * clear class loader cache, so use this method invoke will be no action
     */
    public static void reset(String className) throws RefreshException {
        HotRefreshManager.reset(className);
    }

    /**
     * @deprecated refreshed class has been loaded by app class loader and delete the file cannot
     * clear class loader cache, so use this method invoke will be no action
     */
    public static void reset() throws RefreshException {
        HotRefreshManager.resetAll();
    }
}
