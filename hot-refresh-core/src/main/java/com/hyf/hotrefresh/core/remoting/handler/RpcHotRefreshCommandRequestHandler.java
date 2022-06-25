package com.hyf.hotrefresh.core.remoting.handler;

import com.hyf.hotrefresh.common.util.StringUtils;
import com.hyf.hotrefresh.core.refresh.HotRefreshCommand;
import com.hyf.hotrefresh.core.refresh.HotRefresher;
import com.hyf.hotrefresh.core.remoting.payload.RpcHotRefreshCommandRequest;
import com.hyf.hotrefresh.core.remoting.payload.RpcHotRefreshCommandResponse;
import com.hyf.hotrefresh.remoting.rpc.RpcMessageHandler;

import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/06/25
 */
public class RpcHotRefreshCommandRequestHandler implements RpcMessageHandler<RpcHotRefreshCommandRequest, RpcHotRefreshCommandResponse> {

    @Override
    public RpcHotRefreshCommandResponse handle(RpcHotRefreshCommandRequest request) throws Exception {

        Map<String, Object> headers = request.getHeaders();
        String commandName = (String) headers.get("command");

        HotRefreshCommand command = HotRefreshCommand.getCommand(commandName);

        switch (command) {
            case START:
                HotRefresher.start();
                break;
            case STOP:
                HotRefresher.stop();
                break;
            case RESET:
                String className = (String) headers.get("className");
                if (StringUtils.isNotBlank(className)) {
                    HotRefresher.reset(className);
                }
                else {
                    HotRefresher.reset();
                }
                break;
            default:
                throw new IllegalStateException("Can not happen");
        }

        return new RpcHotRefreshCommandResponse();
    }
}
