
package com.hyf.hotrefresh.client.args;

import com.hyf.hotrefresh.common.args.Argument;
import com.hyf.hotrefresh.common.args.ArgumentParser;

import java.util.List;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2023/04/07
 */
@Argument(value = {"-t", "--token", "--token-name", "--token-type"}, argc = 1)
public class AuthorizationArgumentParser implements ArgumentParser {

    public static final String AUTHORIZATION_TOKEN = "AUTHORIZATION_TOKEN";
    public static final String AUTHORIZATION_TOKEN_NAME = "AUTHORIZATION_TOKEN_NAME";
    public static final String AUTHORIZATION_TOKEN_TYPE = "AUTHORIZATION_TOKEN_TYPE";

    @Override
    public void parse(Map<String, Object> map, String name, List<String> list) {
        if ("-t".equals(name) || "--token".equals(name)) {
            map.put(AUTHORIZATION_TOKEN, list.get(0));
        }
        else if ("--token-name".equals(name)) {
            map.put(AUTHORIZATION_TOKEN_NAME, list.get(0));
        }
        else if ("--token-type".equals(name)) {
            map.put(AUTHORIZATION_TOKEN_TYPE, list.get(0));
        }
    }
}
