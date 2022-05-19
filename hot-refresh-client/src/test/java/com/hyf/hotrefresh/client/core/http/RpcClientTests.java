package com.hyf.hotrefresh.client.core.http;

import com.hyf.hotrefresh.client.core.rpc.RpcClient;
import com.hyf.hotrefresh.core.remoting.payload.RpcHotRefreshRequest;
import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcResponse;
import com.hyf.hotrefresh.remoting.rpc.enums.RpcRequestInst;
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
public class RpcClientTests {

    @Test
    public void testSendRequest() throws IOException {

        RpcClient client = RpcClient.getInstance();

        RpcHotRefreshRequest request = new RpcHotRefreshRequest();
        request.setFileName("Supplier.java");
        request.setFileLocation(null);
        request.setInst(RpcRequestInst.CREATE);
        request.setBody(getJavaFileInputStream());
        Message message = MessageFactory.createMessage(request);

        int port = 2837;
        String url = "http://localhost:" + port;
        startLocalServer(port);

        client.sync(url, message);
    }

    private void startLocalServer(int port) {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                Socket socket = serverSocket.accept();
                try (OutputStream os = socket.getOutputStream()) {
                    os.write("HTTP/1.1 200 OK\r\n\r\n".getBytes(RemotingConstants.DEFAULT_ENCODING.getCharset()));
                    RpcResponse response = new RpcResponse();
                    Message message = MessageFactory.createMessage(response);
                    os.write(MessageCodec.encode(message));
                    os.flush();
                }
                socket.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private InputStream getJavaFileInputStream() {
        String s = "package com.hyf.hotrefresh.adapter.web;\n" +
                "\n" +
                "/**\n" +
                " * @author baB_hyf\n" +
                " * @date 2022/05/14\n" +
                " */\n" +
                "public class Supplier {\n" +
                "\n" +
                "    public static boolean get() {\n" +
                "        return true;\n" +
                "    }\n" +
                "}";

        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }
}
