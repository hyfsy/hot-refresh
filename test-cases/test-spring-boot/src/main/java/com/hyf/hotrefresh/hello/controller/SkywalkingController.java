package com.hyf.hotrefresh.hello.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author baB_hyf
 * @date 2022/07/05
 */
@RestController
@RequestMapping("test/skywalking")
public class SkywalkingController {

    @RequestMapping("1")
    public void getAllMethodAndField() {

        for (Method declaredMethod : this.getClass().getDeclaredMethods()) {
            System.out.println(declaredMethod.getName());
        }

        System.out.println("==========================================================");

        for (Field declaredField : this.getClass().getDeclaredFields()) {
            System.out.println(declaredField.getName());
        }

    }

    @RequestMapping("2")
    public void getParams(String s1, String s2) {
        System.out.println(s1 + " " + s2);
    }
}
