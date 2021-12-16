package com.hyf.hotrefresh;

import com.hyf.hotrefresh.exception.RefreshException;
import com.hyf.hotrefresh.memory.MemoryCode;
import com.hyf.hotrefresh.memory.MemoryCodeCompiler;

import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class HotRefresher {

    public static void refresh(String javaFileName, String javaFileContent, String fileChangeType) throws RefreshException {

        try {

            // TODO 多文件处理顺序
            // TODO 无需编译，直接class

            Map<String, byte[]> compiledBytes = MemoryCodeCompiler.compile(new MemoryCode(javaFileName, javaFileContent));
            compiledBytes = obfuscation(compiledBytes);
            Util.getThrowawayMemoryClassLoader().store(compiledBytes);

            for (Map.Entry<String, byte[]> entry : compiledBytes.entrySet()) {
                String className = entry.getKey();

                // 加载 | 转换
                if ("CREATE".equals(fileChangeType) || "MODIFY".equals(fileChangeType)) {
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(className, false, Util.getOriginContextClassLoader());
                    } catch (ClassNotFoundException ignored) {
                        try {
                            clazz = Class.forName(className, false, Util.getThrowawayMemoryClassLoader());
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException("Class not found: " + className, e);
                        }
                    }

                    if ("MODIFY".equals(fileChangeType)) {
                        HotRefreshManager.reTransform(clazz);
                    }
                }
                // 类卸载
                else if ("DELETE".equals(fileChangeType)) {
                    reset(className);
                }
            }
        } catch (RefreshException e) {
            throw e;
        } catch (Exception e) {
            throw new RefreshException("Hot refresh failed", e);
        }
    }

    public static void reset(String className) throws RefreshException {
        HotRefreshManager.reset(className);
    }

    public static void reset() throws RefreshException {
        HotRefreshManager.resetAll();
    }

    private static Map<String, byte[]> obfuscation(Map<String, byte[]> compiledBytes) throws RefreshException {
        try {
            ServiceLoader<ObfuscationHandler> obfuscationHandlers = ServiceLoader.load(ObfuscationHandler.class);
            for (ObfuscationHandler handler : obfuscationHandlers) {
                compiledBytes = handler.handle(compiledBytes);
            }
        } catch (Exception e) {
            throw new RefreshException("Obfuscation failed", e);
        }

        return compiledBytes;
    }
}
