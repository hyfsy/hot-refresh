package com.hyf.hotrefresh.client.core.client;

import com.hyf.hotrefresh.client.api.client.Client;
import com.hyf.hotrefresh.client.api.core.RequestBuilder;
import com.hyf.hotrefresh.client.api.core.DefaultRequestBuilder;
import com.hyf.hotrefresh.common.Services;
import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.common.util.UrlUtils;
import com.hyf.hotrefresh.remoting.MessageCallback;
import com.hyf.hotrefresh.remoting.MessageCustomizer;
import com.hyf.hotrefresh.remoting.exception.ClientException;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.hyf.hotrefresh.common.Constants.REFRESH_API;

/**
 * @author baB_hyf
 * @date 2022/08/18
 */
public class HttpBasedClient implements Client {

    public static final String API_HOT_REFRESH = REFRESH_API;

    private final List<MessageCustomizer> messageCustomizers;

    private CloseableHttpClient client;

    private RequestBuilder requestBuilder;

    public HttpBasedClient() {
        List<RequestBuilder> builders = Services.gets(RequestBuilder.class);
        this.requestBuilder = builders.isEmpty() ? new DefaultRequestBuilder() : builders.iterator().next();

        this.messageCustomizers = Services.gets(MessageCustomizer.class);
    }

    @Override
    public void start() throws ClientException {
        this.client = createClient();
    }

    @Override
    public void stop() throws ClientException {
        try {
            client.close();
        } catch (IOException e) {
            throw new ClientException("client stop failed", e);
        }
    }

    @Override
    public Message sync(String url, Message request, long timeoutMillis) throws ClientException {

        long l = System.currentTimeMillis();

        CompletableFuture<Message> future = new CompletableFuture<>();

        customizeMessage(request);

        sync(UrlUtils.concat(url, API_HOT_REFRESH), request, new ResponseHandler() {

            @Override
            public void successHandle(HttpResponse response) {

                HttpEntity entity = response.getEntity();
                try (InputStream is = entity.getContent()) {

                    if (is == null) {
                        return; // no content
                    }

                    byte[] data = IOUtils.readAsByteArray(is);
                    Message message = MessageCodec.decode(data);
                    future.complete(message);
                } catch (IOException e) {
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void errorHandle(HttpResponse response) {
                StatusLine sl = response.getStatusLine();
                future.completeExceptionally(new Exception(sl.getStatusCode() + " " + sl.getReasonPhrase()));
            }
        });

        try {
            long timeout = timeoutMillis - (System.currentTimeMillis() - l);
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // if (Log.isDebugMode()) {
            //     Log.debug(ExceptionUtils.getStackMessage(e));
            // }
            // else {
            //     Log.warn("Request to " + serverAddress + " failed: " + ExceptionUtils.getNestedMessage(e));
            // }
            throw new ClientException("Failed to handle request", e);
        }
    }

    @Override
    public void async(String addr, Message message, long timeoutMillis, MessageCallback callback) throws ClientException {
        try {
            Message response = sync(addr, message, timeoutMillis);
            callback.handle(response, null);
        } catch (ClientException e) {
            callback.handle(null, e);
        }
    }

    public void sync(String url, Message request, ResponseHandler callback) throws ClientException {

        HttpUriRequest req = requestBuilder.build(url, request);

        try {
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
        } catch (IOException e) {
            throw new ClientException("Failed to handle request", e);
        }
    }

    protected void customizeMessage(Message message) {
        for (MessageCustomizer customizer : messageCustomizers) {
            customizer.customize(message);
        }
    }

    protected CloseableHttpClient createClient() {

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

        void successHandle(HttpResponse response);

        void errorHandle(HttpResponse response);
    }
}
