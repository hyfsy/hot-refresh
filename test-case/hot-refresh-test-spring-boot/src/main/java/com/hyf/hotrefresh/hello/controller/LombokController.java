package com.hyf.hotrefresh.hello.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author baB_hyf
 * @date 2022/05/13
 */
@Slf4j
@RestController
@RequestMapping("test/lombok")
public class LombokController {

    @RequestMapping("1")
    public void logUse() {

        log.info("log is enabled");
    }
}
