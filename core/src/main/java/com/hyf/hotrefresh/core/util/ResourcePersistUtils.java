package com.hyf.hotrefresh.core.util;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.util.FileUtils;
import com.hyf.hotrefresh.common.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

/**
 * current only support {@link com.hyf.hotrefresh.core.classloader.InfrastructureJarClassLoader} to persist jar.
 * <p>
 * unrecommended for other purposes.
 *
 * @author baB_hyf
 * @date 2021/12/11
 */
public abstract class ResourcePersistUtils {

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

        File localFile = getLocalFile(url, false);

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

        File localFile = getLocalFile(url, true);

        try {
            URLConnection connection = url.openConnection();

            try (InputStream is = connection.getInputStream();
                 OutputStream os = Files.newOutputStream(localFile.toPath())) {
                IOUtils.writeTo(is, os);
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

    private static File getLocalFile(URL url, boolean create) {
        String urlFileName = url.toString().substring(url.toString().lastIndexOf("/"));
        String downloadFilePath = DOWNLOAD_HOME + urlFileName;
        return FileUtils.getFile(downloadFilePath, create);
    }
}
