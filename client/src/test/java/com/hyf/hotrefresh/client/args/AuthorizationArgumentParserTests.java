package com.hyf.hotrefresh.client.args;

import com.hyf.hotrefresh.common.args.ArgumentHolder;
import org.junit.After;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author baB_hyf
 * @date 2023/04/07
 */
public class AuthorizationArgumentParserTests {

    @After
    public void after() {
        ArgumentHolder.remove(AuthorizationArgumentParser.AUTHORIZATION_TOKEN);
        ArgumentHolder.remove(AuthorizationArgumentParser.AUTHORIZATION_TOKEN_NAME);
        ArgumentHolder.remove(AuthorizationArgumentParser.AUTHORIZATION_TOKEN_TYPE);
    }

    @Test
    public void testParser() {
        AuthorizationArgumentParser parser = new AuthorizationArgumentParser();
        Map<String, Object> arguments = new HashMap<>();

        parser.parse(arguments, "-t", Collections.singletonList("token"));
        parser.parse(arguments, "--token-name", Collections.singletonList("Authorization"));
        parser.parse(arguments, "--token-type", Collections.singletonList("cookie"));

        assertEquals("token", arguments.get(AuthorizationArgumentParser.AUTHORIZATION_TOKEN));
        assertEquals("Authorization", arguments.get(AuthorizationArgumentParser.AUTHORIZATION_TOKEN_NAME));
        assertEquals("cookie", arguments.get(AuthorizationArgumentParser.AUTHORIZATION_TOKEN_TYPE));
    }
}
