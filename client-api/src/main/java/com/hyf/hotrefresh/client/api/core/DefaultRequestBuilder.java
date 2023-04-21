package com.hyf.hotrefresh.client.api.core;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.Services;
import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/06/18
 */
public class DefaultRequestBuilder implements RequestBuilder {

    public static final RequestConfig DEFAULT_CONFIG = createDefaultConfig();

    private final List<RequestInterceptor> interceptors;

    public DefaultRequestBuilder() {
        interceptors = Services.gets(RequestInterceptor.class);
    }

    // TODO config
    private static RequestConfig createDefaultConfig() {
        return RequestConfig.custom().setConnectionRequestTimeout(1000)
                .setConnectTimeout(10000).setSocketTimeout(60000).build();
    }

    @Override
    public HttpUriRequest build(String url, Message message) {

        HttpRequest request = new HttpRequest();
        request.setUrl(url);

        interceptors.forEach(interceptor -> interceptor.intercept(request));

        HttpPost post = new HttpPost(getUrl(request));
        post.setConfig(DEFAULT_CONFIG);
        request.getHeaders().forEach(post::addHeader);

        ByteArrayInputStream bais = new ByteArrayInputStream(MessageCodec.encode(message));
        post.setEntity(new InputStreamEntity(bais, bais.available(), ContentType.create(RemotingConstants.DEFAULT_CONTENT_TYPE)));

        return post;
    }

    private String getUrl(HttpRequest request) {
        StringBuilder sb = new StringBuilder(request.getUrl());

        Map<String, String> params = request.getParams();

        if (!params.isEmpty()) {
            boolean urlHasParams = sb.indexOf("?") != -1;
            if (!urlHasParams) {
                sb.append('?');
            }
            else {
                sb.append('&');
            }
            List<String> kvPairs = new LinkedList<>();
            params.forEach((k, v) -> kvPairs.add(urlEncode(k) + "=" + urlEncode(v)));
            sb.append(String.join("&", kvPairs));
        }

        return sb.toString();
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, Constants.MESSAGE_ENCODING.toString());
        } catch (UnsupportedEncodingException e) {
            Log.error("Url encode failed", e);
            return s;
        }
    }
}
