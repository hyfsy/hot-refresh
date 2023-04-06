package com.hyf.hotrefresh.client.command;

import org.junit.Test;

/**
 * @author baB_hyf
 * @date 2023/04/05
 */
public class ArgumentCommandHandlerTests {

    private final ArgumentCommandHandler argumentCommandHandler = new ArgumentCommandHandler();

    @Test
    public void testGetConfig() throws Exception {
        argumentCommandHandler.doHandle(new String[] {"get"});
        argumentCommandHandler.doHandle(new String[] {"set", "watchHome"});
        argumentCommandHandler.doHandle(new String[] {"get", "watchHome"});
        argumentCommandHandler.doHandle(new String[] {"set", "watchHome", "test"});
    }
}
