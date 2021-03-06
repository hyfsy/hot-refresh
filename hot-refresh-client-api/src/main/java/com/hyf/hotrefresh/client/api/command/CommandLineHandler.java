package com.hyf.hotrefresh.client.api.command;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.Services;

import java.util.List;
import java.util.Scanner;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class CommandLineHandler extends Thread {

    private List<CommandHandler> commandHandlers;

    public CommandLineHandler() {
        setName("command-line-handler");
        commandHandlers = Services.gets(CommandHandler.class);
    }

    @Override
    public void run() {

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            command = command.trim();
            if ("".equals(command)) {
                continue;
            }

            for (CommandHandler commandHandler : commandHandlers) {
                if (commandHandler.support(command)) {
                    try {
                        commandHandler.handle(command);
                    } catch (Throwable t) {
                        Log.error("Command handler failed", t);
                    }
                }
            }
        }
    }
}
