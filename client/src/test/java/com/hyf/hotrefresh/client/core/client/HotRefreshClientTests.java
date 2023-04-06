package com.hyf.hotrefresh.client.core.client;

import com.hyf.hotrefresh.client.api.client.Client;
import com.hyf.hotrefresh.remoting.MessageCallback;
import com.hyf.hotrefresh.remoting.exception.ClientException;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcMessageType;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcBatchResponse;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcHeartbeatRequest;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcHeartbeatResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author baB_hyf
 * @date 2023/04/05
 */
public class HotRefreshClientTests {

    private HotRefreshClient client;

    @Before
    public void before() {
        client = HotRefreshClient.getInstance();
    }

    @Test
    public void testClientSPI() {
        assertTrue(MockClient.initialized);
    }

    @Test
    public void testStartStop() {
        client.stop();
        assertFalse(MockClient.started);
        client.start();
        assertTrue(MockClient.started);
        client.start();
        assertTrue(MockClient.started);
        client.stop();
        assertFalse(MockClient.started);
        client.stop();
        assertFalse(MockClient.started);
        client.start();
        assertTrue(MockClient.started);
    }

    @Test
    public void testHeartbeat() {
        client.heartbeat();
        assertTrue(MockClient.isHeartbeat);
    }

    @Test
    public void testSendBatchRequest() {
        client.sendBatchRequest(Collections.singletonList(new RpcHeartbeatRequest()));
        assertTrue(MockClient.isBatch);
    }

    public static class MockClient implements Client {

        private static boolean initialized;
        private static boolean started     = false;
        private static boolean isHeartbeat = false;
        private static boolean isBatch     = false;

        public MockClient() {
            initialized = true;
        }

        @Override
        public void start() throws ClientException {
            if (started) {
                throw new IllegalStateException();
            }
            started = true;
        }

        @Override
        public void stop() throws ClientException {
            if (!started) {
                throw new IllegalStateException();
            }
            started = false;
        }

        @Override
        public Message sync(String addr, Message message, long timeoutMillis) throws ClientException {
            if (message.getMessageType() == RpcMessageType.REQUEST_HEARTBEAT) {
                isHeartbeat = true;
                message.setMessageType(RpcMessageType.RESPONSE_HEARTBEAT);
                message.setBody(new RpcHeartbeatResponse());
                return message;
            }
            else if (message.getMessageType() == RpcMessageType.REQUEST_BATCH) {
                isBatch = true;
                message.setMessageType(RpcMessageType.RESPONSE_BATCH);
                message.setBody(new RpcBatchResponse());
                return message;
            }
            throw new IllegalStateException();
        }

        @Override
        public void async(String addr, Message message, long timeoutMillis, MessageCallback callback) throws ClientException {

        }
    }
}
