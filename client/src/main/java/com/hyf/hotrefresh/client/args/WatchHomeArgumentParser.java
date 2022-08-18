package com.hyf.hotrefresh.client.args;

import com.hyf.hotrefresh.common.args.Argument;
import com.hyf.hotrefresh.common.args.ArgumentParser;

import java.util.List;
import java.util.Map;

import static com.hyf.hotrefresh.common.Constants.ARG_WATCH_HOME;

/**
 * @author baB_hyf
 * @date 2022/06/18
 */
@Argument(value = {"-h", "-H", "--home"}, argc = 1)
public class WatchHomeArgumentParser implements ArgumentParser {

    @Override
    public void init(Map<String, Object> initArgs) {
        String defaultWatchHome = System.getProperty(ARG_WATCH_HOME, System.getProperty("user.dir"));
        initArgs.put(ARG_WATCH_HOME, defaultWatchHome);
    }

    @Override
    public void parse(Map<String, Object> parsedArgs, List<String> segments) {
        String watchHomePath = segments.iterator().next();
        parsedArgs.put(ARG_WATCH_HOME, watchHomePath);
    }
}
