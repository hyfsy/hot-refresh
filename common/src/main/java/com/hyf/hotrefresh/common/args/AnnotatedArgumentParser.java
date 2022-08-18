package com.hyf.hotrefresh.common.args;

/**
 * @author baB_hyf
 * @date 2022/06/18
 * @see ArgumentHolder
 */
public interface AnnotatedArgumentParser extends ArgumentParser {

    String[] value();

    int argc();

}
