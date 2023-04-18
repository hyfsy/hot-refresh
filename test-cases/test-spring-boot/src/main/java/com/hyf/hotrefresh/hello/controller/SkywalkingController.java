package com.hyf.hotrefresh.hello.controller;

import com.hyf.hotrefresh.hello.invoke.TestTrigger;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * -javaagent:C:\Users\Desktop\test\source\skywalking-java\skywalking-agent\skywalking-agent.jar
 * -Dskywalking.agent.service_name=test-application -Dskywalking.agent.is_cache_enhanced_class=true -Dskywalking.agent.is_open_debugging_class=true -Dskywalking.agent.class_cache_mode=FILE
 *
 * @author baB_hyf
 * @date 2023/04/08
 */
@RestController
@RequestMapping("test/skywalking")
public class SkywalkingController<TTT> {

    static {
        new Exception("Print static method invocation").printStackTrace();
    }

    private String str;

    public SkywalkingController() {
        new Exception("Print constructor invocation").printStackTrace();
    }

    public SkywalkingController(ApplicationContext s) {
        new Exception("Print constructor invocation s").printStackTrace();
    }

    @RequestMapping("1")
    public String printClassInfo() {
        System.out.println("====================================");
        Class<?> clazz = getClass();
        System.out.println("className: " + clazz.getName());
        System.out.println("====================================");
        for (Class<?> anInterface : clazz.getInterfaces()) {
            System.out.println("interface: " + anInterface.getName());
        }
        System.out.println("====================================");
        for (Constructor<?> declaredConstructor : clazz.getDeclaredConstructors()) {
            System.out.println("constructor: " + declaredConstructor);
        }
        System.out.println("====================================");
        for (Field declaredField : clazz.getDeclaredFields()) {
            System.out.println("field: " + declaredField);
        }
        System.out.println("====================================");
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            System.out.println("method: " + declaredMethod);
        }
        System.out.println("====================================");
        new Exception("Print method invocation").printStackTrace();

        return "success";
    }

    @RequestMapping("2")
    public Integer paramMethodWithShowException(@RequestParam String s, @RequestParam String s2) {
        throw new RuntimeException();
    }

    @RequestMapping("3")
    public Integer genericMethod(List<String> s) {
        return 0;
    }

    @RequestMapping("4")
    public void staticMethod() {
        invokeStatic();
    }

    private static void invokeStatic() {
        System.out.println(TraceContext.traceId());
    }

    @RequestMapping("5")
    public void trigger() {
        TestTrigger.trigger();
    }
}
