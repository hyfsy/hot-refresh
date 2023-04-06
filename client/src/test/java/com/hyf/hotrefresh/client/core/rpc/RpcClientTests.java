package com.hyf.hotrefresh.client.core.rpc;

import com.hyf.hotrefresh.common.util.IOUtils;
import com.hyf.hotrefresh.core.remoting.payload.RpcHotRefreshRequest;
import com.hyf.hotrefresh.core.remoting.payload.RpcHotRefreshRequestInst;
import com.hyf.hotrefresh.remoting.client.DefaultRpcClient;
import com.hyf.hotrefresh.remoting.constants.RemotingConstants;
import com.hyf.hotrefresh.remoting.message.Message;
import com.hyf.hotrefresh.remoting.message.MessageCodec;
import com.hyf.hotrefresh.remoting.message.MessageFactory;
import com.hyf.hotrefresh.remoting.rpc.payload.RpcResponse;
import org.junit.Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class RpcClientTests {

    public static void main(String[] args) throws Exception {

    // }
    //
    // @Test
    // public void testSendRequest() throws IOException {

        RpcClient client = RpcClient.getInstance();

        RpcHotRefreshRequest request = new RpcHotRefreshRequest();
        request.setFileName("Supplier.java");
        request.setFileLocation(null);
        request.setInst(RpcHotRefreshRequestInst.CREATE);
        request.setBody(getJavaFileInputStream());
        Message message = MessageFactory.createMessage(request);

        DefaultRpcClient defaultRpcClient = new DefaultRpcClient();
        Message request1 = defaultRpcClient.request("localhost:2837", message, 10000L);
        System.out.println(request1);

        // Socket socket = new Socket();
        // socket.setSoTimeout(20000);
        // ByteArrayInputStream bais = new ByteArrayInputStream(MessageCodec.encode(message));
        // socket.connect(new InetSocketAddress("localhost", 2837));
        //
        // new Thread(() -> {
        //     try {
        //         OutputStream os = socket.getOutputStream();
        //         IOUtils.writeTo(bais, os);
        //         os.flush();
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //     }
        // }).start();
        //
        //
        // new Thread(() -> {
        // try {
        //     InputStream is = socket.getInputStream();
        //     ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //     IOUtils.writeTo(is, baos);
        //     Message msg = MessageCodec.decode(baos.toByteArray());
        //     System.out.println(msg);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        // }).start();



        // int port = 2837;
        // String url = "http://localhost:" + port;
        // // startLocalServer(port);
        //
        // client.sync(url, message);
    }

    private void startLocalServer(int port) {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port);
                 Socket socket = serverSocket.accept()) {
                try (InputStream is = socket.getInputStream();
                     OutputStream os = socket.getOutputStream()) {
                    byte[] bytes = IOUtils.readAsByteArray(is);
                    bytes = findBody(bytes);
                    Message request = MessageCodec.decode(bytes);
                    RpcResponse response = new RpcResponse();
                    Message message = MessageFactory.createResponseMessage(request, response);
                    os.write("HTTP/1.1 200 OK\r\n\r\n".getBytes(RemotingConstants.DEFAULT_ENCODING.getCharset()));
                    os.write(MessageCodec.encode(message));
                    os.flush();
                }
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

    public static InputStream getJavaFileInputStream() {
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
