package com.hyf.hotrefresh.util;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class HttpUtil {

    // TODO async、ssl
    private static final CloseableHttpClient CLIENT = HttpClients.createDefault();

    private static final RequestConfig DEFAULT_CONFIG = RequestConfig.custom().setConnectionRequestTimeout(5000)
            .setConnectTimeout(5000).setSocketTimeout(5000).build();

    public static InputStream upload(String url, Map<String, File> fileMap) throws IOException {

        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(DEFAULT_CONFIG);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create().setLaxMode();
        if (fileMap != null) {
            fileMap.forEach((n, f) -> {
                if (f.exists()) {
                    builder.addPart(n, new FileBody(f));
                }
            });
        }
        httpPost.setEntity(builder.build());

        CloseableHttpResponse execute = CLIENT.execute(httpPost);
        HttpEntity entity = execute.getEntity();
        StatusLine sl = execute.getStatusLine();
        if (sl.getStatusCode() >= 300) {
            throw new IOException(sl.getStatusCode() + " " + sl.getReasonPhrase());
        }
        return entity.getContent();
    }
}
