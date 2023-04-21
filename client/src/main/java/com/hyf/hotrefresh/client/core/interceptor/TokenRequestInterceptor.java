package com.hyf.hotrefresh.client.core.interceptor;

import com.hyf.hotrefresh.client.api.core.HttpRequest;
import com.hyf.hotrefresh.client.api.core.RequestInterceptor;
import com.hyf.hotrefresh.client.args.AuthorizationArgumentParser;
import com.hyf.hotrefresh.common.args.ArgumentHolder;
import com.hyf.hotrefresh.common.util.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author baB_hyf
 * @date 2023/04/07
 */
public class TokenRequestInterceptor implements RequestInterceptor {

    private static final String DEFAULT_TOKEN_NAME = "Authorization";
    private static final String DEFAULT_TOKEN_TYPE = TokenType.HEADER.name();

    @Override
    public void intercept(HttpRequest request) {
        String token = ArgumentHolder.get(AuthorizationArgumentParser.AUTHORIZATION_TOKEN);
        if (StringUtils.isBlank(token)) {
            return;
        }

        String tokenType = ArgumentHolder.getOrDefault(AuthorizationArgumentParser.AUTHORIZATION_TOKEN_TYPE, DEFAULT_TOKEN_TYPE);
        String tokenName = ArgumentHolder.getOrDefault(AuthorizationArgumentParser.AUTHORIZATION_TOKEN_NAME, DEFAULT_TOKEN_NAME);

        if (TokenType.HEADER.name().equalsIgnoreCase(tokenType)) {
            if (DEFAULT_TOKEN_NAME.equals(tokenName)) {
                token = "Bearer " + token;
            }
            request.getHeaders().put(tokenName, token);
        }
        else if (TokenType.COOKIE.name().equalsIgnoreCase(tokenType)) {
            String cookie = request.getHeaders().get("Cookie");
            if (StringUtils.isBlank(cookie)) {
                request.getHeaders().put("Cookie", tokenName + "=" + token);
            }
            else {
                cookie += "; " + tokenName + "=" + token;
                request.getHeaders().put("Cookie", cookie);
            }
        }
        else if (TokenType.PARAM.name().equalsIgnoreCase(tokenType)) {
            request.getParams().put(tokenName, token);
        }
        else {
            throw new IllegalArgumentException("token type [" + Arrays.stream(TokenType.values()).map(TokenType::name).collect(Collectors.joining(",")) + "] not support: " + tokenType);
        }
    }

    public enum TokenType {
        HEADER,
        COOKIE,
        PARAM,
        ;
    }
}
