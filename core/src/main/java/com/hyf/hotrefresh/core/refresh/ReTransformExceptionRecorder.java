package com.hyf.hotrefresh.core.refresh;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.util.ByteUtils;
import com.hyf.hotrefresh.core.extend.ClassBytesDumper;
import com.hyf.hotrefresh.core.util.InfraUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author baB_hyf
 * @date 2022/05/27
 */
public class ReTransformExceptionRecorder {

    private static ThreadLocal<byte[]>   originThreadLocal              = new ThreadLocal<>();
    private static ThreadLocal<Class<?>> classBeingRedefinedThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<byte[]>   currentThreadLocal             = new ThreadLocal<>();
    private static ThreadLocal<byte[]>   transformedThreadLocal         = new ThreadLocal<>();

    static void record(byte[] origin, byte[] current, byte[] transformed) {
        originThreadLocal.set(origin);
        currentThreadLocal.set(current);
        transformedThreadLocal.set(transformed);
    }

    static void record(Class<?> classBeingRedefined, byte[] current, byte[] transformed) {
        classBeingRedefinedThreadLocal.set(classBeingRedefined);
        currentThreadLocal.set(current);
        transformedThreadLocal.set(transformed);
    }

    static void clear() {
        originThreadLocal.remove();
        classBeingRedefinedThreadLocal.remove();
        currentThreadLocal.remove();
        transformedThreadLocal.remove();
    }

    static String buildBytesMessage() {

        if (!Log.isDebugMode()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        byte[] origin = originThreadLocal.get();
        Class<?> classBeingRedefined = classBeingRedefinedThreadLocal.get();
        byte[] current = currentThreadLocal.get();
        byte[] transformed = transformedThreadLocal.get();

        sb.append("\r\n");

        if (origin != null) {
            sb.append("\r\n\t-> origin bytes: ").append(ByteUtils.toString(origin));
        }

        if (classBeingRedefined != null) {
            try {

                // dump class bytes
                byte[] originBytes = null;
                try {
                    originBytes = ClassBytesDumper.dump(classBeingRedefined);
                    String className = InfraUtils.getClassName(originBytes);
                    String classFilePath = HotRefreshTransformer.DEBUG_STORE_PATH + File.separator + className.replace(".", File.separator) + ".class";
                    ClassBytesDumper.dump(originBytes, classFilePath);
                } catch (IOException e) {
                    Log.error("Failed to dump bytes", e);
                }

                if (originBytes != null) {
                    sb.append("\r\n\t-> origin class bytes: ").append(ByteUtils.toString(originBytes));
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        if (current != null) {
            sb.append("\r\n\t-> current bytes: ").append(ByteUtils.toString(current));
        }

        if (transformed != null) {
            sb.append("\r\n\t-> transformed bytes: ").append(ByteUtils.toString(transformed));
        }

        sb.append("\r\n");

        return sb.toString();
    }
}
