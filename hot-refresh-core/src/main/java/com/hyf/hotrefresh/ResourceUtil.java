package com.hyf.hotrefresh;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class ResourceUtil {

    public static final String DOWNLOAD_HOME = System.getProperty("user.home") + File.separator + ".hot-refresh" + File.separator + "lib";

    public static URL getResourceURL(URL url) {
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
        }
        else {
            return null;
        }
    }

    private static URL downloadToLocal(URL url) {

        File localFile = getLocalFile(url);

        try {
            URLConnection connection = url.openConnection();

            try (InputStream bis = new BufferedInputStream(connection.getInputStream());
                 BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(localFile))) {

                int len;
                byte[] buf = new byte[1024];
                while ((len = bis.read(buf)) != -1) {
                    bos.write(buf, 0, len);
                }

                bos.flush();
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
        File file = new File(downloadFilePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }
}
