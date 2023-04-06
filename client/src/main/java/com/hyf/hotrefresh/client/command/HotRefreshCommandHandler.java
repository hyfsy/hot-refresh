package com.hyf.hotrefresh.client.command;

import com.hyf.hotrefresh.client.api.command.AbstractCommandHandler;
import com.hyf.hotrefresh.client.core.client.HotRefreshClient;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.util.StringUtils;
import com.hyf.hotrefresh.core.refresh.HotRefreshCommand;
import com.hyf.hotrefresh.core.remoting.payload.RpcHotRefreshCommandRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/06/25
 */
public class HotRefreshCommandHandler extends AbstractCommandHandler {

    private static final HotRefreshClient client = HotRefreshClient.getInstance();

    @Override
    protected String getIdentity() {
        return "hotrefresh";
    }

    @Override
    protected void doHandle(String[] commands) throws Exception {

        if (commands.length < 1) {
            Log.warn("Unknown command");
            return;
        }

        String commandName = commands[0];

        HotRefreshCommand command = HotRefreshCommand.getCommand(commandName);

        RpcHotRefreshCommandRequest request = new RpcHotRefreshCommandRequest();

        Map<String, Object> headers = new HashMap<>();
        headers.put("command", command.getCommand());

        if (command.equals(HotRefreshCommand.RESET)) {
            if (commands.length > 1) {
                String className = commands[1];
                if (StringUtils.isNotBlank(className)) {
                    headers.put("className", className);
                }
            }
        }

        request.setHeaders(headers);
        client.sendRequest(request);
    }
}
