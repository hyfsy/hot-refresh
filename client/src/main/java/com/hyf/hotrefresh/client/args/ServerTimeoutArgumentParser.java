package com.hyf.hotrefresh.client.args;

import com.hyf.hotrefresh.common.args.Argument;
import com.hyf.hotrefresh.common.args.ArgumentParser;

import java.util.List;
import java.util.Map;

import static com.hyf.hotrefresh.common.Constants.ARG_SERVER_TIMEOUT;

/**
 * @author baB_hyf
 * @date 2022/08/18
 */
@Argument(value = "--server-timeout", argc = 1)
public class ServerTimeoutArgumentParser implements ArgumentParser {

    @Override
    public void init(Map<String, Object> initArgs) {
        // 30s
        initArgs.put(ARG_SERVER_TIMEOUT, 3000L);
    }

    @Override
    public void parse(Map<String, Object> parsedArgs, List<String> segments) {
        parsedArgs.put(ARG_SERVER_TIMEOUT, segments.iterator().next());
    }
}
