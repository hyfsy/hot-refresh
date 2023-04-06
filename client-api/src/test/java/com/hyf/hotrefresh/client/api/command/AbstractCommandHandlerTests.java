package com.hyf.hotrefresh.client.api.command;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author baB_hyf
 * @date 2023/04/05
 */
public class AbstractCommandHandlerTests {

    @Test
    public void testSupportAndHandle() throws Exception {
        AtomicInteger i = new AtomicInteger(0);
        AbstractCommandHandler abstractCommandHandler = new AbstractCommandHandler() {

            @Override
            protected String getIdentity() {
                return "mock";
            }

            @Override
            protected int commandSize() {
                return 1;
            }

            @Override
            protected void doHandle(String[] commands) throws Exception {
                assertEquals("1", commands[0]);
            }
        };

        assertTrue(abstractCommandHandler.support("mock"));
        abstractCommandHandler.handle("mock 1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCommandSize() throws Exception {
        AbstractCommandHandler abstractCommandHandler = new AbstractCommandHandler() {

            @Override
            protected String getIdentity() {
                return "mock";
            }

            @Override
            protected int commandSize() {
                return 2;
            }

            @Override
            protected void doHandle(String[] commands) throws Exception {
                if (commands.length != 2) {
                    throw new IllegalArgumentException();
                }
            }
        };
        abstractCommandHandler.handle("mock 1");
    }
}
