package com.hyf.hotrefresh.client.core.rpc;

import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.core.remoting.payload.RpcHotRefreshRequest;
import com.hyf.hotrefresh.core.remoting.payload.RpcHotRefreshRequestInst;
import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import com.hyf.hotrefresh.remoting.exception.ServerException;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcResponse;
import com.hyf.hotrefresh.remoting.server.embedded.EmbeddedRpcServer;
import com.hyf.hotrefresh.remoting.server.embedded.EmbeddedServerConfig;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class RpcClientTests2 {

    public static void main(String[] args) throws Exception {
        int port = 2837;
        // new RpcClientTests2().startLocalServer(port);
        EmbeddedServerConfig embeddedServerConfig = new EmbeddedServerConfig();
        embeddedServerConfig.setListenPort(port);
        EmbeddedRpcServer embeddedRpcServer = new EmbeddedRpcServer(embeddedServerConfig);
        embeddedRpcServer.start();


        Thread.currentThread().join();
    }

    // @Test
    public void testSendRequest() throws IOException, ServerException {
        int port = 2837;
        startLocalServer(port);
    }

    private void startLocalServer(int port) {
        new Thread(() -> {
            while (true) {
                try (ServerSocket serverSocket = new ServerSocket(port);
                     ) {
                    Socket socket = serverSocket.accept();
                    new Thread(() -> {
                    try (InputStream is = socket.getInputStream();
                         ) {
                        byte[] bytes = IOUtils.readAsByteArray(is);
                        // bytes = findBody(bytes);
                        System.out.println(new String(bytes, "UTF-8"));
                        Message request = MessageCodec.decode(bytes);
                        System.out.println(request);
                        RpcResponse response = new RpcResponse();
                        Message message = MessageFactory.createResponseMessage(request, response);
                        OutputStream os = socket.getOutputStream();
                        os.write("HTTP/1.1 200 OK\r\n\r\n".getBytes(RemotingConstants.DEFAULT_ENCODING.getCharset()));
                        os.write(MessageCodec.encode(message));
                        os.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private byte[] findBody(byte[] bytes) {
        int idx = 0;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == '\r') {
                i++;
                if (bytes[i] == '\n') {
                    i++;
                    if (bytes[i] == '\r') {
                        i++;
                        if (bytes[i] == '\n') {
                            idx = ++i;
                            break;
                        }
                        else {
                            i--;
                        }
                        i--;
                    }
                }
            }
        }
        if (idx == 0) {
            return new byte[0];
        }
        else {
            byte[] newBytes = new byte[bytes.length - idx];
            System.arraycopy(bytes, idx, newBytes, 0, newBytes.length);
            return newBytes;
        }
    }
}
