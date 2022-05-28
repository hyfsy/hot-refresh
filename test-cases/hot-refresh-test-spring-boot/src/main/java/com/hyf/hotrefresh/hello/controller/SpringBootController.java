package com.hyf.hotrefresh.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author baB_hyf
 * @date 2022/05/13
 */
@RestController
@RequestMapping("test/springboot")
public class SpringBootController {

    // @Value("${server.port}")
    // private Integer port;

    @Autowired
    private ApplicationContext context;

    @RequestMapping("1")
    public void _1() {
        // System.out.println(port);
        System.out.println(context);
    }

    // @RequestMapping("2")
    // public boolean _2() {
    //     return true;
    // }
}
