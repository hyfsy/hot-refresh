package com.hyf.hotrefresh.client.core.interceptor;

import com.hyf.hotrefresh.client.api.core.HttpRequest;
import com.hyf.hotrefresh.client.args.AuthorizationArgumentParser;
import com.hyf.hotrefresh.common.args.ArgumentHolder;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author baB_hyf
 * @date 2023/04/07
 */
public class TokenRequestInterceptorTests {

    @After
    public void after() {
        ArgumentHolder.remove(AuthorizationArgumentParser.AUTHORIZATION_TOKEN);
        ArgumentHolder.remove(AuthorizationArgumentParser.AUTHORIZATION_TOKEN_NAME);
        ArgumentHolder.remove(AuthorizationArgumentParser.AUTHORIZATION_TOKEN_TYPE);
    }

    @Test
    public void testIntercept() {
        HttpRequest request = new HttpRequest();

        TokenRequestInterceptor interceptor = new TokenRequestInterceptor();
        interceptor.intercept(request);

        assertNull(request.getUrl());
        assertTrue(request.getHeaders().isEmpty());
        assertTrue(request.getParams().isEmpty());

        ArgumentHolder.put(AuthorizationArgumentParser.AUTHORIZATION_TOKEN, "xxx");
        ArgumentHolder.put(AuthorizationArgumentParser.AUTHORIZATION_TOKEN_NAME, "xxx");
        ArgumentHolder.put(AuthorizationArgumentParser.AUTHORIZATION_TOKEN_TYPE, "xxx");

        try {
            interceptor.intercept(request);
            throw new IllegalStateException();
        } catch (Exception e) {
            assertSame(e.getClass(), IllegalArgumentException.class);
        }

        ArgumentHolder.put(AuthorizationArgumentParser.AUTHORIZATION_TOKEN_TYPE, "header");
        interceptor.intercept(request);
        assertEquals("xxx", request.getHeaders().get("xxx"));

        assertNull(request.getParams().get("abc"));
        ArgumentHolder.put(AuthorizationArgumentParser.AUTHORIZATION_TOKEN_TYPE, "param");
        ArgumentHolder.put(AuthorizationArgumentParser.AUTHORIZATION_TOKEN_NAME, "abc");
        interceptor.intercept(request);
        assertNotNull(request.getParams().get("abc"));

        ArgumentHolder.put(AuthorizationArgumentParser.AUTHORIZATION_TOKEN_TYPE, "cookie");
        assertNull(request.getHeaders().get("Cookie"));
        interceptor.intercept(request);
        assertEquals(request.getHeaders().get("Cookie"), "abc=xxx");
        interceptor.intercept(request);
        assertEquals(request.getHeaders().get("Cookie"), "abc=xxx; abc=xxx");
    }

}
