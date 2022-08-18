package com.hyf.hotrefresh.plugin.execute.command;

import com.hyf.hotrefresh.client.api.command.CommandHandler;
import com.hyf.hotrefresh.client.core.client.HotRefreshClient;
import com.hyf.hotrefresh.plugin.execute.exception.ExecutionException;
import com.hyf.hotrefresh.plugin.execute.payload.RpcExecutableRequest;

import java.io.File;
import java.nio.file.Files;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class ExecuteCommandHandler implements CommandHandler {

    private static final HotRefreshClient client = HotRefreshClient.getInstance();

    @Override
    public boolean support(String command) {
        return command.startsWith("execute");
    }

    @Override
    public void handle(String command) throws Exception {

        String[] commands = command.split(" ");
        if (commands.length != 2) {
            return;
        }

        String filePath = commands[1];
        File file = new File(filePath);
        if (!file.exists()) {
            throw new ExecutionException("file not exists: " + file.getAbsolutePath());
        }

        RpcExecutableRequest request = new RpcExecutableRequest();
        request.setFileName(file.getName());
        request.setFileLocation(file.getAbsolutePath());
        request.setBody(Files.newInputStream(file.toPath()));
        client.sendRequest(request);
    }
}
