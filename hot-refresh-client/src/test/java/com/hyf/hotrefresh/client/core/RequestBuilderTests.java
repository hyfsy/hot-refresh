package com.hyf.hotrefresh.client.core;

import com.hyf.hotrefresh.client.api.core.RequestBuilder;
import com.hyf.hotrefresh.client.core.rpc.RpcClient;
import com.hyf.hotrefresh.remoting.message.Message;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author baB_hyf
 * @date 2022/06/18
 */
public class RequestBuilderTests {

    @Test
    public void testCustomRequestBuilder() throws IOException {
        assertFalse(MockRequestBuilder.invoked);
        try {
            RpcClient.getInstance().sync("http://www.baidu.com", new Message());
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException); // build return null
        }
        assertTrue(MockRequestBuilder.invoked);
    }

    public static class MockRequestBuilder implements RequestBuilder {

        public static boolean invoked = false;

        @Override
        public HttpUriRequest build(String url, Message message) {
            invoked = true;
            return null;
        }
    }
}
