package com.hyf.hotrefresh.hello;

import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
@RestController
@RequestMapping("test")
public class TestController {

    @Resource
    private ApplicationContext context;

    @RequestMapping("1")
    public String loadOuterClass() {
        try {
            // see resource directory
            Class<?> clazz = ClassUtils.forName("com.hyf.hotrefresh.Test", null);
            return clazz.getName();
        } catch (ClassNotFoundException e) {
        }

        return "error";
    }

    @RequestMapping("2")
    public String invokeOuterClass() {
        try {
            Class<?> clazz = ClassUtils.forName("com.hyf.hotrefresh.hello.Test", null);
            Method main = clazz.getMethod("main", String[].class);
            main.invoke(null, (Object) null);
            return clazz.getName();
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        }

        return "error";
    }

    @RequestMapping("3")
    public String modifySelf() {
        return "3";
    }

    @RequestMapping("4")
    public String testCompileParameters(String param) {
        return "4";
    }
}
