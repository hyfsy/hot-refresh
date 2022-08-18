package com.hyf.hotrefresh.test.execute;

/**
 * @author baB_hyf
 * @date 2022/05/21
 */
public class ExecuteD {

    public static void main(String[] args) {
        System.out.println("execute d");
        throw new RuntimeException("test execute d exception");
    }
}
