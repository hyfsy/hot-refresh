package com.hyf.hotrefresh.client.api.core;

import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcHeartbeatRequest;
import org.junit.Test;

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
}
