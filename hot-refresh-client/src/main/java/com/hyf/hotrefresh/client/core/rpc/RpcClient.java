package com.hyf.hotrefresh.client.core.rpc;

import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandler;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandlerFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class RpcClient {

    private static final RpcClient INSTANCE = new RpcClient();

    private final CloseableHttpClient client = createClient();
    private final RequestConfig       config = createDefaultConfig();

    private final MessageHandler clientMessageHandler = MessageHandlerFactory.getClientMessageHandler();

    private RpcClient() {
    }

    public static RpcClient getInstance() {
        return INSTANCE;
    }

    public void sync(String url, Message request) throws IOException {
        sync(url, request, new ResponseHandler() {

            @Override
            public void successHandle(HttpResponse response) throws Exception {
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();

                if (is == null) {
                    return; // no content
                }

                try {
                    byte[] data = IOUtils.readAsByteArray(is);
                    Message message = MessageCodec.decode(data);
                    clientMessageHandler.handle(message);
                } finally {
                    is.close();
                }
            }

            @Override
            public void errorHandle(HttpResponse response) throws Exception {
                StatusLine sl = response.getStatusLine();
                throw new Exception(sl.getStatusCode() + " " + sl.getReasonPhrase());
            }
        });
    }

    private void sync(String url, Message request, ResponseHandler callback) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);

        ByteArrayInputStream bais = new ByteArrayInputStream(MessageCodec.encode(request));
        httpPost.setEntity(new InputStreamEntity(bais, bais.available(), ContentType.create(RemotingConstants.DEFAULT_CONTENT_TYPE)));

        client.execute(httpPost, response -> {
            StatusLine sl = response.getStatusLine();
            if (sl.getStatusCode() >= 300) {
                try {
                    callback.errorHandle(response);
                } catch (Exception e) {
                    throw new IOException(e);
                }
            }
            else {
                try {
                    callback.successHandle(response);
                } catch (Exception e) {
                    throw new IOException(e);
                }
            }
            return null;
        });
    }

    private CloseableHttpClient createClient() {

        SSLContext sslContext;
        try {
            sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, (chain, authType) -> true)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new RuntimeException("Get SSLContext instance failed");
        }

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, null, null, NoopHostnameVerifier.INSTANCE);

        Registry registry = RegistryBuilder.create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", csf)
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(3000); // 最大连接数3000
        connectionManager.setDefaultMaxPerRoute(400); // 路由链接数400

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(60000)
                .setConnectTimeout(60000)
                .setConnectionRequestTimeout(10000)
                .build();

        return HttpClients.custom().setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .evictExpiredConnections()
                .evictIdleConnections(30, TimeUnit.SECONDS)
                .build();
    }

    private RequestConfig createDefaultConfig() {
        return RequestConfig.custom().setConnectionRequestTimeout(5000)
                .setConnectTimeout(5000).setSocketTimeout(5000).build();
    }

    public interface ResponseHandler {

        void successHandle(HttpResponse response) throws Exception;

        void errorHandle(HttpResponse response) throws Exception;
    }
}
