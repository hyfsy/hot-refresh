package com.hyf.hotrefresh.memory;

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
class PackageFinder {

    private static final String CLASS_FILE_EXTENSION = ".class";

    private final ClassLoader classLoader;

    PackageFinder(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    List<JavaFileObject> find(String packageName) throws IOException {
        String javaPackageName = packageName.replaceAll("\\.", "/");

        List<JavaFileObject> result = new ArrayList<>();

        Enumeration<URL> urlEnumeration = classLoader.getResources(javaPackageName);
        while (urlEnumeration.hasMoreElements()) { // one URL for each jar on the classpath that has the given package
            URL packageFolderURL = urlEnumeration.nextElement();
            result.addAll(listUnder(packageName, packageFolderURL));
        }

        return result;
    }

    private Collection<JavaFileObject> listUnder(String packageName, URL packageFolderURL) {
        File directory = new File(packageFolderURL.getFile());
        // .class
        if (directory.isDirectory()) {
            return processDir(packageName, directory);
        }
        // .jar
        else {
            return processJar(packageFolderURL);
        }
    }

    private List<JavaFileObject> processJar(URL packageFolderURL) {
        List<JavaFileObject> result = new ArrayList<>();
        try {
            String jarUri = packageFolderURL.toExternalForm().substring(0, packageFolderURL.toExternalForm().lastIndexOf("!/"));

            JarURLConnection jarConn = (JarURLConnection) packageFolderURL.openConnection();
            String rootEntryName = jarConn.getEntryName();
            int rootEnd = rootEntryName.length() + 1;

            Enumeration<JarEntry> entryEnum = jarConn.getJarFile().entries();
            while (entryEnum.hasMoreElements()) {
                JarEntry jarEntry = entryEnum.nextElement();
                String name = jarEntry.getName();
                if (name.startsWith(rootEntryName) && name.indexOf('/', rootEnd) == -1 && name.endsWith(CLASS_FILE_EXTENSION)) {
                    URI uri = URI.create(jarUri + "!/" + name);
                    String binaryName = name.replaceAll("/", ".");
                    binaryName = binaryName.replaceAll(CLASS_FILE_EXTENSION + "$", "");

                    result.add(new CustomJavaFileObject(binaryName, uri));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Wasn't able to open " + packageFolderURL + " as a jar file", e);
        }
        return result;
    }

    private List<JavaFileObject> processDir(String packageName, File directory) {
        List<JavaFileObject> result = new ArrayList<>();

        File[] childFiles = directory.listFiles();
        if (childFiles != null) {
            for (File childFile : childFiles) {
                if (childFile.isFile()) {
                    // only .class
                    if (childFile.getName().endsWith(CLASS_FILE_EXTENSION)) {
                        String binaryName = packageName + "." + childFile.getName();
                        binaryName = binaryName.replaceAll(CLASS_FILE_EXTENSION + "$", "");

                        result.add(new CustomJavaFileObject(binaryName, childFile.toURI()));
                    }
                }
            }
        }

        return result;
    }
}
