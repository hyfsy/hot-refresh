package com.hyf.hotrefresh.core.refresh;

/**
 * @author baB_hyf
 * @date 2022/06/25
 */
public enum HotRefreshCommand {

    START("start"),
    STOP("stop"),
    RESET("reset"),
    ;

    private String command;

    HotRefreshCommand(String command) {
        this.command = command;
    }

    public static HotRefreshCommand getCommand(String commandName) {
        for (HotRefreshCommand command : values()) {
            if (command.command.equals(commandName)) {
                return command;
            }
        }

        throw new IllegalArgumentException("Command not support: " + commandName);
    }

    public String getCommand() {
        return command;
    }
}
