package com.hyf.hotrefresh.client.command;

import com.hyf.hotrefresh.client.plugin.PluginClassLoader;
import com.hyf.hotrefresh.common.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.ServiceLoader;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class CommandLineHandler extends Thread {

    private List<CommandHandler> commandHandlers = new ArrayList<>();

    public CommandLineHandler() {
        setName("command-line-handler");

        PluginClassLoader classLoader = PluginClassLoader.getInstance();
        ServiceLoader<CommandHandler> handlers = ServiceLoader.load(CommandHandler.class, classLoader);
        handlers.forEach(commandHandlers::add);
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
