package com.hyf.hotrefresh.plugin.execute;

/**
 * @author baB_hyf
 * @date 2022/05/17
 */
public interface Executable<T> {

    T execute() throws Throwable;

}
