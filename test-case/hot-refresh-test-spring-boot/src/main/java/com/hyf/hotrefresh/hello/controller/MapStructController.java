package com.hyf.hotrefresh.hello.controller;

import com.hyf.hotrefresh.hello.convert.HelloConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;

/**
 * @author baB_hyf
 * @date 2022/05/13
 */
@RestController
@RequestMapping("test/mapstruct")
public class MapStructController {

    @RequestMapping("1")
    public void _1() {
        HelloConverter converter = HelloConverter.INSTANCE;
        for (Method declaredMethod : converter.getClass().getDeclaredMethods()) {
            System.out.println(declaredMethod.getName());
        }
    }
}
