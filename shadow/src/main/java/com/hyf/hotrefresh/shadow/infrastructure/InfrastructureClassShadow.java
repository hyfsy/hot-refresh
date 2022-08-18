package com.hyf.hotrefresh.shadow.infrastructure;

import org.reflections.Reflections;

import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Set;

public class InfrastructureClassShadow {

    public static void main(String[] args) {

        Reflections reflections = new Reflections(InfrastructureConstants.PACKAGE_PATH);
        Set<Class<?>> infrastructureClassSet = reflections.getTypesAnnotatedWith(Infrastructure.class);

        ClassLoader ccl = Thread.currentThread().getContextClassLoader(); // maven plugin set to load target dir

        URL rootResource = ccl.getResource("");
        if (rootResource == null) {
            return;
        }

        for (Class<?> infrastructureClass : infrastructureClassSet) {

            URL resource = ccl.getResource(infrastructureClass.getName().replace(".", "/") + ".class");
            if (resource == null) {
                continue;
            }

            // only this module class
            if (!resource.getFile().startsWith(rootResource.getFile())) {
                continue;
            }

            System.out.println("[INFO] Transform infrastructure class: " + infrastructureClass.getName());

            File infraClassFile = new File(resource.getFile());

            handleInnerClassFileIfNecessary(infraClassFile);

            renameToHotRefreshInfrastructureFile(infraClassFile);
        }
    }

    private static void handleInnerClassFileIfNecessary(File infraClassFile) {
        File classDir = infraClassFile.getParentFile();
        File[] children = classDir.listFiles();
        if (children == null || children.length == 0) {
            return;
        }

        String innerClassFilePrefix = removeClassSuffix(infraClassFile.getAbsolutePath()) + "$";
        for (File child : children) {
            String childPath = child.getAbsolutePath();
            // inner class
            if (childPath.startsWith(innerClassFilePrefix) && !childPath.endsWith(InfrastructureConstants.FILE_SUFFIX)) {
                renameToHotRefreshInfrastructureFile(child);
            }
        }
    }

    private static void renameToHotRefreshInfrastructureFile(File infraClassFile) {
        File renameInfraClassFile = new File(removeClassSuffix(infraClassFile.getAbsolutePath()) + InfrastructureConstants.FILE_SUFFIX);
        copy(infraClassFile, renameInfraClassFile);
    }

    private static String removeClassSuffix(String path) {
        return path.substring(0, path.lastIndexOf(".class"));
    }

    private static void copy(File source, File target) {
        if (target.exists()) {
            if (!target.delete()) {
                System.out.println("[WARN] Failed to delete file: " + target.getAbsolutePath());
            }
        }
        // a module use other module infra class will lead to compile failed, so here must keep source file
        if (!doCopy(source, target)) {
            System.out.println("[WARN] Failed to copy file: " + source.getAbsolutePath());
        }
    }

    private static boolean doCopy(File source, File target) {
        try (FileChannel inChannel = new FileInputStream(source).getChannel();
             FileChannel outChannel = new FileOutputStream(target).getChannel()) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("Write file not exists: " + source.getAbsolutePath());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            throw new RuntimeException("Fail to copy file: " + source.getAbsolutePath(), e);
        }
    }
}
