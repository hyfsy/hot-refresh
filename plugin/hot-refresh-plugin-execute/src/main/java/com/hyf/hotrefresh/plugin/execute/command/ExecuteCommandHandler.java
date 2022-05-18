package com.hyf.hotrefresh.plugin.execute.command;

import com.hyf.hotrefresh.client.core.HotRefreshClient;
import com.hyf.hotrefresh.client.command.CommandHandler;
import com.hyf.hotrefresh.common.util.FileUtils;
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
        String filePath = commands[1];
        File file = FileUtils.getFile(filePath);

        RpcExecutableRequest request = new RpcExecutableRequest();
        request.setFileName(file.getName());
        request.setFileLocation(file.getAbsolutePath());
        request.setContent(Files.newInputStream(file.toPath()));
        client.sendRequest(request);
    }
}
