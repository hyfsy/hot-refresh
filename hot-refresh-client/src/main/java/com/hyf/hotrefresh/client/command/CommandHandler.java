package com.hyf.hotrefresh.client.command;

import com.hyf.hotrefresh.common.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.ServiceLoader;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public interface CommandHandler {

    boolean support(String command);

    void handle(String command) throws Exception;

}

