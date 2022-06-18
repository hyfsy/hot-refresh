package com.hyf.hotrefresh.client.args;

import com.hyf.hotrefresh.common.args.Argument;
import com.hyf.hotrefresh.common.args.ArgumentParser;
import com.hyf.hotrefresh.common.util.UrlUtils;

import java.util.List;
import java.util.Map;

import static com.hyf.hotrefresh.common.Constants.ARG_SERVER_URL;

/**
 * @author baB_hyf
 * @date 2022/06/18
 */
@Argument(value = {"-s", "-S", "--server"}, argc = 1)
public class ServerURLArgumentParser implements ArgumentParser {

    @Override
    public void init(Map<String, Object> initArgs) {
        String defaultServerURL = UrlUtils.clean(System.getProperty(ARG_SERVER_URL, "http://localhost:8080"));
        initArgs.put(ARG_SERVER_URL, defaultServerURL);
    }

    @Override
    public void parse(Map<String, Object> parsedArgs, List<String> segments) {
        String serverURL = UrlUtils.clean(segments.iterator().next());
        parsedArgs.put(ARG_SERVER_URL, serverURL);
    }
}