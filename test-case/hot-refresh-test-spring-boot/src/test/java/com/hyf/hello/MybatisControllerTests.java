package com.hyf.hello;

import com.hyf.hotrefresh.exception.RefreshException;
import com.hyf.hotrefresh.hello.controller.MybatisController;
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
@SpringBootTest(classes = MybatisController.class)
public class MybatisControllerTests {

    @Resource
    private MybatisController mybatisController;

    @Test
    public void testAddCompiledMethod() throws RefreshException {
    }
}
