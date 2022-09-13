package com.hyf.hotrefresh.client.command;

import com.hyf.hotrefresh.client.api.command.CommandHandler;

import java.util.Arrays;

/**
 * @author baB_hyf
 * @date 2022/09/09
 */
public class ClientCommandHandler implements CommandHandler {

    @Override
    public boolean support(String command) {
        return Arrays.asList("exit", "quit", "q").contains(command);
    }

    @Override
    public void handle(String command) throws Exception {
        switch (command) {
            case "exit":
            case "quit":
            case "q":
                System.exit(0);
                break;
        }
    }
}
