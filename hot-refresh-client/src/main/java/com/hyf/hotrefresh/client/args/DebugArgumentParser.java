package com.hyf.hotrefresh.client.args;

import com.hyf.hotrefresh.common.args.Argument;
import com.hyf.hotrefresh.common.args.ArgumentParser;

import java.util.List;
import java.util.Map;

import static com.hyf.hotrefresh.common.Constants.ARG_DEBUG;

/**
 * @author baB_hyf
 * @date 2022/06/18
 */
@Argument({"-d", "-D", "--debug"})
public class DebugArgumentParser implements ArgumentParser {

    @Override
    public void init(Map<String, Object> initArgs) {
        initArgs.put(ARG_DEBUG, Integer.getInteger(ARG_DEBUG, 0) != 0);
    }

    @Override
    public void parse(Map<String, Object> parsedArgs, List<String> segments) {
        parsedArgs.put(ARG_DEBUG, true);
    }
}
