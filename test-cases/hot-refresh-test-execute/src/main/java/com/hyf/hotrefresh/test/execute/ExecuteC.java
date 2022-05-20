package com.hyf.hotrefresh.test.execute;

import com.hyf.hotrefresh.plugin.execute.Executable;

public class ExecuteC implements Executable<ExecuteC.A> {

    @Override
    public A execute() {
        System.out.println("execute c");
        return new A();
    }

    public static class A {
        private String a = "b";

        public String getA() {
            return a;
        }
    }
}
