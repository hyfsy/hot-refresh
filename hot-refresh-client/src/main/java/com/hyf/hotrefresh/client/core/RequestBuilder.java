package com.hyf.hotrefresh.client.core;

import com.hyf.hotrefresh.remoting.message.Message;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * @author baB_hyf
 * @date 2022/06/18
 */
public interface RequestBuilder {

    HttpUriRequest build(String url, Message message);

}
