package com.hyf.hotrefresh.core.memory;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.Log;

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
class DependencyLookup {

    private static final String CLASS_FILE_EXTENSION = ".class";

    private final ClassLoader classLoader;

    DependencyLookup(ClassLoader classLoader) {
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
        // decode file path for chinese
        if (directory.getAbsolutePath().contains("%")) {
            try {
                directory = new File(URLDecoder.decode(directory.getAbsolutePath(), Constants.MESSAGE_ENCODING.name()));
            } catch (UnsupportedEncodingException e) {
                Log.error("Failed to decode file for chinese path", e);
            }
        }

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
            if (rootEntryName == null) {
                Log.warn("Wasn't able to open " + packageFolderURL + " as a jar file");
                return result;
            }

            int rootEnd = rootEntryName.length() + 1;

            Enumeration<JarEntry> entryEnum = jarConn.getJarFile().entries();
            while (entryEnum.hasMoreElements()) {
                JarEntry jarEntry = entryEnum.nextElement();
                String name = jarEntry.getName();
                if (name.startsWith(rootEntryName) && name.indexOf('/', rootEnd) == -1 && name.endsWith(CLASS_FILE_EXTENSION)) {
                    URI uri = URI.create(jarUri + "!/" + name);
                    String binaryName = name.replaceAll("/", ".");
                    binaryName = binaryName.replaceAll(CLASS_FILE_EXTENSION + "$", "");

                    result.add(new URIJavaFileObject(binaryName, uri));
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

                        result.add(new URIJavaFileObject(binaryName, childFile.toURI()));
                    }
                }
            }
        }

        return result;
    }
}
