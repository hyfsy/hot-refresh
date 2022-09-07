package com.hyf.hotrefresh.test.execute;

import com.hyf.hotrefresh.plugin.execute.Executable;

/**
 * execute E:\study\code\idea4\project\hot-refresh\test-cases\test-execute\src\main\java\com\hyf\hotrefresh\test\execute\ExecuteA.java
 */
public class ExecuteA implements Executable<String> {

    @Override
    public String execute() throws Exception {
        System.out.println("execute a");
        return "executed";
    }
}
