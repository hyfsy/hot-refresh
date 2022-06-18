package com.hyf.hotrefresh.client.core.rpc;

import com.hyf.hotrefresh.client.core.DefaultRequestBuilder;
import com.hyf.hotrefresh.client.api.core.RequestBuilder;
import com.hyf.hotrefresh.common.Services;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandler;
import com.hyf.hotrefresh.remoting.message.handler.MessageHandlerFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class RpcClient {

    private static final RpcClient INSTANCE = new RpcClient();

    private final CloseableHttpClient client = createClient();

    private final MessageHandler clientMessageHandler = MessageHandlerFactory.getClientMessageHandler();

    private RequestBuilder requestBuilder;

    private RpcClient() {
        List<RequestBuilder> builders = Services.gets(RequestBuilder.class);
        if (builders.isEmpty()) {
            requestBuilder = new DefaultRequestBuilder();
        }
        else {
            requestBuilder = builders.iterator().next();
        }
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
                    IOUtils.close(is);
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

        HttpUriRequest req = requestBuilder.build(url, request);

        client.execute(req, response -> {
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
        connectionManager.setMaxTotal(50); // 最大连接数
        connectionManager.setDefaultMaxPerRoute(10); // 并行进行连接数

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(5000) // 连接获取超时时间
                .setConnectTimeout(20000) // 连接超时时间
                .setSocketTimeout(60000) // 读取超时时间
                .build();

        return HttpClients.custom().setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .evictExpiredConnections()
                .evictIdleConnections(30, TimeUnit.SECONDS)
                .build();
    }

    public interface ResponseHandler {

        void successHandle(HttpResponse response) throws Exception;

        void errorHandle(HttpResponse response) throws Exception;
    }
}
