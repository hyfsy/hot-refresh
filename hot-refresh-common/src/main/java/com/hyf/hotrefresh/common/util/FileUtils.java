package com.hyf.hotrefresh.common.util;

import com.hyf.hotrefresh.common.Log;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public abstract class FileUtils {

    public static String safeRead(Path path) {
        return safeRead(path.toFile());
    }

    public static String safeRead(String path) {
        return safeRead(new File(path));
    }

    public static String safeRead(File file) {
        try (InputStream is = new FileInputStream(file)) {
            return IOUtils.readAsString(is);
        } catch (FileNotFoundException e) {
            Log.error("Read file not exists: " + file.getAbsolutePath(), e);
            return "";
        } catch (IOException e) {
            throw new RuntimeException("Fail to read file: " + file.getAbsolutePath(), e);
        }
    }

    public static boolean safeWrite(File file, InputStream is) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            safeWrite(is, fos);
            return true;
        } catch (FileNotFoundException e) {
            Log.error("Write file not exists: " + file.getAbsolutePath(), e);
            return false;
        } catch (IOException e) {
            throw new RuntimeException("Fail to write file: " + file.getAbsolutePath(), e);
        }
    }

    public static boolean safeWrite(InputStream is, OutputStream os) {
        try {
            IOUtils.writeTo(is, os);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Fail to write", e);
        }
    }

    public static void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length != 0) {
                for (File f : files) {
                    delete(f);
                }
            }
        }

        file.delete();
    }

    public static File getFile(String path) {
        return getFile(path, false);
    }

    public static File getFile(String path, boolean create) {
        File file = new File(path);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (create && !file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to create file", e);
            }
        }
        return file;
    }

    public static boolean copy(File source, File target) {
        try (FileChannel inChannel = new FileInputStream(source).getChannel();
            FileChannel outChannel = new FileOutputStream(target).getChannel()) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
            return true;
        } catch (FileNotFoundException e) {
            Log.error("Write file not exists: " + source.getAbsolutePath(), e);
            return false;
        } catch (IOException e) {
            throw new RuntimeException("Fail to copy file: " + source.getAbsolutePath(), e);
        }
    }
}
