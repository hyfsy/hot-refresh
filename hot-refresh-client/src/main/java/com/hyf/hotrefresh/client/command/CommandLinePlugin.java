package com.hyf.hotrefresh.client.command;

import com.hyf.hotrefresh.client.plugin.Pluggable;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class CommandLinePlugin implements Pluggable {

    @Override
    public void setup() throws Exception {
        new CommandLineHandler().start();
    }
}
