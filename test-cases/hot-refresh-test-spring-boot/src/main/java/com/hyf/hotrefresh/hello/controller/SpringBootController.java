package com.hyf.hotrefresh.hello.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author baB_hyf
 * @date 2022/05/13
 */
@RestController
@RequestMapping("test/springboot")
public class SpringBootController {

    @RequestMapping("1")
    public void _1() {
    }
}
