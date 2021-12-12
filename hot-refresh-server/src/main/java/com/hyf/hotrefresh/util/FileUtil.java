package com.hyf.hotrefresh.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class FileUtil {

    // private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    public static String safeRead(Path path) {
        return safeRead(path.toFile());
    }

    public static String safeRead(String path) {
        return safeRead(new File(path));
    }

    public static String safeRead(File file) {
        try {
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            // if (log.isDebugEnabled()) {
            // log.debug("Read file not exists: {}", file.getAbsoluteFile());
            // }
            // if (log.isTraceEnabled()) {
            // log.trace("Read file not exists", e);
            // }
            System.out.println("Read file not exists: " + file.getAbsolutePath());
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            throw new RuntimeException("Fail to read file: " + file.getAbsolutePath(), e);
        }
    }
}
