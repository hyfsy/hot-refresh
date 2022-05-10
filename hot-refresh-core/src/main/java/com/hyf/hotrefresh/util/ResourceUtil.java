package com.hyf.hotrefresh.util;

import com.hyf.hotrefresh.Constants;
import com.hyf.hotrefresh.util.FileUtil;
import com.hyf.hotrefresh.util.IOUtil;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class ResourceUtil {

    public static final String DOWNLOAD_HOME = Constants.REFRESH_HOME + File.separator + "lib";

    public static URL getResourceURL(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("url must not be null");
        }

        URL localURL = getLocalURL(url);

        if (localURL == null) {
            localURL = downloadToLocal(url);
        }

        return localURL;
    }

    private static URL getLocalURL(URL url) {

        File localFile = getLocalFile(url);

        if (localFile.exists()) {
            try {
                return localFile.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException("Failed to get file url", e);
            }
        } else {
            return null;
        }
    }

    private static URL downloadToLocal(URL url) {

        File localFile = getLocalFile(url);

        try {
            URLConnection connection = url.openConnection();

            try (InputStream is = connection.getInputStream();
                 OutputStream os = Files.newOutputStream(localFile.toPath())) {
                IOUtil.writeTo(is, os);
            }

            try {
                return localFile.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException("Download path illegal: " + localFile.getAbsolutePath(), e);
            }

        } catch (IOException e) {
            throw new RuntimeException("Download jar failed", e);
        }
    }

    private static File getLocalFile(URL url) {
        String urlFileName = url.toString().substring(url.toString().lastIndexOf("/"));
        String downloadFilePath = DOWNLOAD_HOME + urlFileName;
        return FileUtil.getFile(downloadFilePath);
    }
}
