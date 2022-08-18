package com.hyf.hotrefresh.client.core;

import com.hyf.hotrefresh.client.api.core.RequestBuilder;
import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;

import java.io.ByteArrayInputStream;

/**
 * @author baB_hyf
 * @date 2022/06/18
 */
public class DefaultRequestBuilder implements RequestBuilder {

    public static final RequestConfig DEFAULT_CONFIG = createDefaultConfig();

    private static RequestConfig createDefaultConfig() {
        return RequestConfig.custom().setConnectionRequestTimeout(1000)
                .setConnectTimeout(10000).setSocketTimeout(60000).build();
    }

    @Override
    public HttpUriRequest build(String url, Message message) {

        HttpPost post = new HttpPost(url);
        post.setConfig(DEFAULT_CONFIG);

        ByteArrayInputStream bais = new ByteArrayInputStream(MessageCodec.encode(message));
        post.setEntity(new InputStreamEntity(bais, bais.available(), ContentType.create(RemotingConstants.DEFAULT_CONTENT_TYPE)));

        return post;
    }
}
