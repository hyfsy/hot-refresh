package com.hyf.hotrefresh.client.args;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.args.ArgumentHolder;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author baB_hyf
 * @date 2022/06/18
 */
public class ArgumentParserTests {

    @Test
    public void testInitArgument() {
        assertFalse(ArgumentHolder.get(Constants.ARG_DEBUG));
        assertEquals(ArgumentHolder.get(Constants.ARG_WATCH_HOME), System.getProperty("user.dir"));
        assertEquals(ArgumentHolder.get(Constants.ARG_SERVER_URL), "http://localhost:8080");

        String[] args = new String[]{"-d", "-s", "http", "-h", "E:"};
        ArgumentHolder.parse(args);

        assertTrue(ArgumentHolder.get(Constants.ARG_DEBUG));
        assertEquals(ArgumentHolder.get(Constants.ARG_WATCH_HOME), "E:");
        assertEquals(ArgumentHolder.get(Constants.ARG_SERVER_URL), "http");
    }
}
