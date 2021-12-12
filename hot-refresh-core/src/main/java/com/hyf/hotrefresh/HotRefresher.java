package com.hyf.hotrefresh;

import com.hyf.hotrefresh.exception.RefreshException;
import com.hyf.hotrefresh.memory.MemoryClassLoader;
import com.hyf.hotrefresh.memory.MemoryCompiler;

import java.lang.instrument.Instrumentation;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class HotRefresher {

    static {
        HotRefreshManager.class.getName();
    }

    public static void refresh(String javaFileContent, String name, String type) throws RefreshException {

        try {

            MemoryClassLoader mcl = Util.getThrowawayMemoryClassLoader();

            Map<String, byte[]> compiledBytes = MemoryCompiler.compile(name, javaFileContent);

            for (Map.Entry<String, byte[]> entry : compiledBytes.entrySet()) {
                String className = entry.getKey();
                byte[] bytes = entry.getValue();

                // 添加到缓存中
                mcl.store(name, bytes);

                // 加载 | 转换
                if ("CREATE".equals(type) || "MODIFY".equals(type)) {
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(className, false, mcl.getParent());
                    } catch (ClassNotFoundException e) {
                        try {
                            clazz = Class.forName(className, false, mcl);
                        } catch (ClassNotFoundException cnfe) {
                            throw new RuntimeException("Class not found: " + className, cnfe);
                        }
                    }

                    if ("MODIFY".equals(type)) {
                        try {
                            Instrumentation instrumentation = HotRefreshManager.getInstrumentation();
                            instrumentation.retransformClasses(clazz);
                        } catch (Exception e) {
                            throw new RuntimeException("Class info has been modified", e);
                        }
                    }
                }
                // 类卸载
                else if ("DELETE".equals(type)) {
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
}
