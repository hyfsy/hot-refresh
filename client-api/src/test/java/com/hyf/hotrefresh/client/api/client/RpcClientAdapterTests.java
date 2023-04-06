package com.hyf.hotrefresh.client.api.client;

import com.hyf.hotrefresh.remoting.MessageCallback;
import com.hyf.hotrefresh.remoting.client.RpcClient;
import com.hyf.hotrefresh.remoting.exception.ClientException;
import com.hyf.hotrefresh.remoting.exception.RemotingException;
import com.hyf.hotrefresh.remoting.message.Message;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author baB_hyf
 * @date 2023/04/05
 */
public class RpcClientAdapterTests {

    @Test
    public void testUse() {
        RpcClientAdapter rpcClientAdapter = new RpcClientAdapter(new MockRpcClient());
        rpcClientAdapter.start();
        rpcClientAdapter.stop();
        rpcClientAdapter.sync(null, null, 0L);
        rpcClientAdapter.async(null, null, 0L, null);

        assertTrue(MockRpcClient.start);
        assertTrue(MockRpcClient.stop);
        assertTrue(MockRpcClient.request);
        assertTrue(MockRpcClient.requestAsync);
    }

    public static class MockRpcClient implements RpcClient {

        private static boolean start        = false;
        private static boolean stop         = false;
        private static boolean request      = false;
        private static boolean requestAsync = false;

        @Override
        public void start() throws ClientException {
            start = true;
        }

        @Override
        public void stop() throws ClientException {
            stop = true;
        }

        @Override
        public Message request(String addr, Message message, long timeoutMillis) throws RemotingException {
            request = true;
            return null;
        }

        @Override
        public void requestAsync(String addr, Message message, long timeoutMillis, MessageCallback callback) throws RemotingException {
            requestAsync = true;
        }
    }
}
