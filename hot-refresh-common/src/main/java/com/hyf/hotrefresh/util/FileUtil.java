package com.hyf.hotrefresh.util;

import com.hyf.hotrefresh.Log;

import java.io.*;
import java.nio.file.Path;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class FileUtil {

    public static String safeRead(Path path) {
        return safeRead(path.toFile());
    }

    public static String safeRead(String path) {
        return safeRead(new File(path));
    }

    public static String safeRead(File file) {
        try (InputStream is = new FileInputStream(file)) {
            return IOUtil.readAsString(is);
        } catch (FileNotFoundException e) {
            Log.error("Read file not exists: " + file.getAbsolutePath(), e);
            return "";
        } catch (IOException e) {
            throw new RuntimeException("Fail to read file: " + file.getAbsolutePath(), e);
        }
    }

    public static File getFile(String path) {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }
}
