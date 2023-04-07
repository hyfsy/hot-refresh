package com.hyf.hotrefresh.client.api.core;

import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcHeartbeatRequest;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author baB_hyf
 * @date 2023/04/05
 */
public class DefaultRequestBuilderTests {

    @Test
    public void testInvoke() {
        Message message = MessageFactory.createMessage(new RpcHeartbeatRequest());
        new DefaultRequestBuilder().build("xxx", message);
    }

    @Test
    public void testInterceptor() {
        Message message = MessageFactory.createMessage(new RpcHeartbeatRequest());
        HttpUriRequest xxx = new DefaultRequestBuilder().build("xxx", message);
        assertTrue(MockInterceptor.invoked);
        assertEquals(1, xxx.getHeaders("invoked").length);
    }

    public static class MockInterceptor implements RequestInterceptor {

        private static boolean invoked = false;

        @Override
        public void intercept(HttpRequest request) {
            invoked = true;
            request.getHeaders().put("invoked", "true");
        }
    }
}
