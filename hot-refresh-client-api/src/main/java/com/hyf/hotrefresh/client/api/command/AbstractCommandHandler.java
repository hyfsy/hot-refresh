package com.hyf.hotrefresh.client.api.command;

/**
 * @author baB_hyf
 * @date 2022/06/25
 */
public abstract class AbstractCommandHandler implements CommandHandler {

    @Override
    public boolean support(String command) {
        boolean identityEquals = command.startsWith(getIdentity());
        String[] s = command.split(" ");
        boolean sizeEquals = commandSize() == -1 || s.length == commandSize();
        return identityEquals && sizeEquals;
    }

    protected abstract String getIdentity();

    @Override
    public void handle(String command) throws Exception {
        String[] commandStrings = command.split(" ");
        String[] commands = new String[commandStrings.length - 1];
        System.arraycopy(commandStrings, 1, commands, 0, commandStrings.length - 1);
        doHandle(commands);
    }

    protected int commandSize() {
        return -1;
    }

    protected abstract void doHandle(String[] commands) throws Exception;
}
