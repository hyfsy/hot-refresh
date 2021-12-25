package com.hyf.hotrefresh;

import com.hyf.hotrefresh.exception.RefreshException;
import com.hyf.hotrefresh.memory.MemoryCode;
import com.hyf.hotrefresh.memory.MemoryCodeCompiler;

import java.util.Map;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class HotRefresher {

    public static void refresh(String javaFileName, String javaFileContent, String fileChangeType) throws RefreshException {
        try {

            // TODO 多文件处理
            // TODO 无需编译，直接class

            Map<String, byte[]> compiledBytes = MemoryCodeCompiler.compile(new MemoryCode(javaFileName, javaFileContent));
            Util.getThrowawayMemoryClassLoader().store(compiledBytes);

            for (Map.Entry<String, byte[]> entry : compiledBytes.entrySet()) {
                String className = entry.getKey();

                ChangeType changeType = ChangeType.valueOf(fileChangeType);

                // 加载
                if (ChangeType.CREATE == changeType) {
                }
                // 转换
                else if (ChangeType.MODIFY == changeType) {
                    try {
                        Class<?> clazz = Class.forName(className, false, Util.getOriginContextClassLoader());
                        HotRefreshManager.reTransform(clazz);
                    } catch (ClassNotFoundException ignored) {
                        // prevent modify event but class not exist
                        Class.forName(className, false, Util.getThrowawayMemoryClassLoader());
                    }
                }
                // 卸载
                else if (ChangeType.DELETE == changeType) {
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
