package com.hyf.hotrefresh.hello.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
@RestController
@RequestMapping("test")
public class HelloController {

    @Resource
    private ApplicationContext context;

    @RequestMapping("1")
    public boolean loadOuterClass() {
        try {
            // see resource directory
            ClassUtils.forName("com.hyf.hotrefresh.hello.Test", null);
            return true;
        } catch (Throwable e) {
        }

        return false;
    }

    @RequestMapping("2")
    public boolean invokeOuterClass() {
        try {
            Class<?> clazz = ClassUtils.forName("com.hyf.hotrefresh.hello.Test", null);
            Method getMethod = clazz.getMethod("get");
            return (boolean) getMethod.invoke(null);
        } catch (Throwable e) {
        }

        return false;
    }

    @RequestMapping("3")
    public boolean modifySelf() {
        return false;
    }

    @RequestMapping("4")
    public String compileParameters(String param) {
        return "4";
    }
}
