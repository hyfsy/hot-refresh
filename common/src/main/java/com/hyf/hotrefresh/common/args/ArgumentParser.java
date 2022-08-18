package com.hyf.hotrefresh.common.args;

import java.util.List;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/06/18
 * @see ArgumentHolder
 */
public interface ArgumentParser {

    default void init(Map<String, Object> initArgs) {
    }

    void parse(Map<String, Object> parsedArgs, List<String> segments);

}
