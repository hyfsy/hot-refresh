package com.hyf.hotrefresh.plugin.arthas.command;

import com.hyf.hotrefresh.client.api.command.AbstractCommandHandler;
import com.hyf.hotrefresh.client.core.HotRefreshClient;
import com.hyf.hotrefresh.plugin.arthas.remoting.payload.RpcArthasCommandRequest;

import java.util.HashMap;
import java.util.Map;

public class ArthasCommandHandler extends AbstractCommandHandler {

    private static final HotRefreshClient client = HotRefreshClient.getInstance();

    @Override
    protected String getIdentity() {
        return "arthas";
    }

    @Override
    protected void doHandle(String[] commands) throws Exception {

        RpcArthasCommandRequest request = new RpcArthasCommandRequest();
        Map<String, Object> headers = new HashMap<>();
        headers.put("command", "start");
        request.setHeaders(headers);
        client.sendRequest(request);

        // 客户端逻辑：
        // 用户在当前命令行发送 arthas
        // 本地提交一个请求给服务端，通知开启其本地的telnet
        // 开启失败，则返回错误给命令行
        // 开启成功，本地启动telnet，将当前命令行与telnet对接
        // 命令统一发送到本地的telnet
        // 本地处理该输入和输出，发送对应的command请求给服务端，服务端返回响应给本地的telnet，完成本地调用远程的arthas

        // 服务端逻辑：
        // 客户端发送开启telnet的请求，服务端校验是否已开启过，维护之前的状态，方便关闭的时候校验
        // 将请求和响应对接到服务端的telnet
        // 通过arthas的Bootstrap开启本地的telnet，将arthas的开启操作作为响应返回
        // 本地telnet接受响应进行命令操作，发送请求到服务端telnet
        // 服务端telnet接受命令执行后，将输出发送到响应，返回给客户端
        // TODO 长连接问题怎么处理？
        //  服务端主动发送请求（不太现实，服务端找不到本地客户端ip）？
        //  还是说放到下一次响应中返回，客户端心跳请求
    }
}
