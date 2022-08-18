package com.hyf.hotrefresh.client.command;

import com.hyf.hotrefresh.client.api.command.CommandLineHandler;
import com.hyf.hotrefresh.client.api.plugin.Plugin;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class CommandLinePlugin implements Plugin {

    @Override
    public void setup() throws Exception {
        new CommandLineHandler().start();
    }
}
