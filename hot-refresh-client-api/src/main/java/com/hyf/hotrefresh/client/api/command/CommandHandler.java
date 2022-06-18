package com.hyf.hotrefresh.client.api.command;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public interface CommandHandler {

    boolean support(String command);

    void handle(String command) throws Exception;

}

