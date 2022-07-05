package com.hyf.hotrefresh.shadow.infrastructure;

import org.reflections.Reflections;

import java.io.File;
import java.net.URL;
import java.util.Set;

public class InfrastructureClassShadow {

    public static void main(String[] args) {

        Reflections reflections = new Reflections(InfrastructureConstants.PACKAGE_PATH);
        Set<Class<?>> infrastructureClassSet = reflections.getTypesAnnotatedWith(Infrastructure.class);

        ClassLoader ccl = Thread.currentThread().getContextClassLoader(); // maven plugin set to load target dir

        for (Class<?> infrastructureClass : infrastructureClassSet) {

            URL resource = ccl.getResource(infrastructureClass.getName().replace(".", "/") + ".class");
            if (resource == null) {
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
        renameTo(infraClassFile, renameInfraClassFile);
    }

    private static String removeClassSuffix(String path) {
        return path.substring(0, path.lastIndexOf(".class"));
    }

    private static void renameTo(File source, File target) {
        if (target.exists()) {
            if (!target.delete()) {
                System.out.println("[WARN] Failed to delete file: " + target.getAbsolutePath());
            }
        }
        if (!source.renameTo(target)) {
            System.out.println("[WARN] Failed to rename file: " + source.getAbsolutePath());
        }
    }
}
