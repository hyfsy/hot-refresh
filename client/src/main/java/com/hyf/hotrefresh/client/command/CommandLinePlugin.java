package com.hyf.hotrefresh.client.command;

import com.hyf.hotrefresh.client.api.plugin.Plugin;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class CommandLinePlugin implements Plugin {

    private CommandLineHandler commandLineHandler;

    @Override
    public void install() throws Exception {
        this.commandLineHandler = new CommandLineHandler();
        this.commandLineHandler.start();
    }

    @Override
    public void uninstall() {
        // TODO ?
        this.commandLineHandler.interrupt();
    }
}
