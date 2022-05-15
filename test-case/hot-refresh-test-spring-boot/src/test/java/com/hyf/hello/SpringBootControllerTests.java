package com.hyf.hello;

import com.hyf.hotrefresh.core.exception.RefreshException;
import com.hyf.hotrefresh.hello.controller.SpringBootController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringBootController.class)
public class SpringBootControllerTests {

    @Resource
    private SpringBootController springBootController;

    @Test
    public void testAddCompiledMethod() throws RefreshException {
    }
}
