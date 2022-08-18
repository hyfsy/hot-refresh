package com.hyf.hotrefresh.adapter.lombok.util;

import com.hyf.hotrefresh.common.util.FileUtils;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.common.util.StringUtils;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * @author baB_hyf
 * @date 2022/06/25
 */
public abstract class LombokAnatomizeUtils {

    public static File anatomize(String jarFilePath) {
        File jarFile = new File(jarFilePath);
        String absolutePath = jarFile.getAbsolutePath();
        if (!absolutePath.endsWith(".jar")) {
            throw new RuntimeException("Not a jar file");
        }

        try {
            File tempDirectory = unjar(jarFile);
            replaceName(tempDirectory);
            File newJarFile = jar(tempDirectory, jarFile);
            FileUtils.delete(tempDirectory);
            return newJarFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed anatomize lombok jar", e);
        }
    }

    public static File unjar(File jarFile) throws IOException {
        File parentFile = jarFile.getParentFile();
        File tempDirectory = new File(parentFile, "temp");
        tempDirectory.mkdirs();

        String dirPath = tempDirectory.getAbsolutePath();
        try (JarInputStream jis = new JarInputStream(new FileInputStream(jarFile))) {

            ZipEntry entry;
            while ((entry = jis.getNextEntry()) != null) {

                String filePath = dirPath + File.separator + entry.getName();

                if (StringUtils.isBlank(filePath)) {
                    continue;
                }

                File file = new File(filePath);

                // directory
                if (entry.isDirectory()) {
                    continue;
                }

                file.getParentFile().mkdirs();
                file.createNewFile();

                // file
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
                    IOUtils.writeTo(jis, bos);
                } catch (Exception e) {
                    throw new IOException("Failed to read jar file content", e);
                }

                jis.closeEntry();
            }
        }

        return tempDirectory;
    }

    public static void replaceName(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null && files.length != 0) {
                for (File f : files) {
                    replaceName(f);
                }
            }
        }
        else {
            String lombokSign = ".SCL.lombok";
            if (directory.getName().endsWith(lombokSign)) {
                String newFilePath = directory.getAbsolutePath().substring(0, directory.getAbsolutePath().length() - lombokSign.length()) + ".class";
                if (!directory.renameTo(new File(newFilePath))) {
                    throw new RuntimeException("Failed to rename file: " + newFilePath);
                }
            }
        }
    }

    public static File jar(File tempDirectory, File jarFile) throws IOException {
        String absolutePath = jarFile.getAbsolutePath();
        String prefix = absolutePath.substring(0, absolutePath.length() - 4);
        String newFilePath = prefix + "-anatomized.jar";
        File newFile = new File(newFilePath);
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(newFile))) {
            File[] files = tempDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    jar(file, jos, "");
                }
            }
        }
        return newFile;
    }

    private static void jar(File tempDirectory, JarOutputStream jos, String jarPath) throws IOException {
        if (tempDirectory.isDirectory()) {
            jos.putNextEntry(new ZipEntry(jarPath + tempDirectory.getName() + "/"));
            File[] files = tempDirectory.listFiles();
            if (files != null && files.length > 0) {
                for (File child : files) {
                    jar(child, jos, jarPath + tempDirectory.getName() + "/");
                }
            }
        }
        else {
            JarEntry zipEntry = new JarEntry(jarPath + tempDirectory.getName());
            jos.putNextEntry(zipEntry);
            try (FileInputStream fis = new FileInputStream(tempDirectory)) {
                IOUtils.writeTo(fis, jos);
            }
            jos.closeEntry();
        }
    }
}
