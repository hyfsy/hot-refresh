package com.hyf.hotrefresh.hello.controller;

import com.hyf.hotrefresh.hello.convert.HelloConverter;
import com.hyf.hotrefresh.hello.convert.MixedConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author baB_hyf
 * @date 2022/05/13
 */
@RestController
@RequestMapping("test/mapstruct")
public class MapStructController {

    @RequestMapping("1")
    public boolean addCompiledMethod() {
        HelloConverter converter = HelloConverter.INSTANCE;
        return converter.getClass().getDeclaredMethods().length > 0;
    }

    @RequestMapping("2")
    public boolean mixCompiledMethod() {
        MixedConverter converter = MixedConverter.INSTANCE;
        return converter.getClass().getDeclaredMethods().length > 0;
    }
}
