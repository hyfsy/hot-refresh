package com.hyf.hotrefresh.hello.invoke;

public class TestInvoker {

    static {
        // new Exception("staticMethod").printStackTrace();
        System.out.println("staticMethod");
    }

    public static void invoke() {
        System.out.println("invoke");
        TestInvoker testInvoker = new TestInvoker();
        testInvoker.testStaticMethod();
        testInvoker.testInstanceMethod();
    }

    private String str;

    private TestInvoker() {
        // new Exception("constructor").printStackTrace();
        System.out.println("constructor");
    }


    private void testInstanceMethod() {
        // new Exception("instanceMethod").printStackTrace();
        System.out.println("instanceMethod");
    }

    private void testStaticMethod() {
        System.out.println("testStaticMethod");
        testInvokeStatic();
    }

    private static void testInvokeStatic() {
        System.out.println("invokeStatic");
    }
}
