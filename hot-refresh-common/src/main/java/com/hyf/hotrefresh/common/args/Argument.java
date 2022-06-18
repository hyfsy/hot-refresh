package com.hyf.hotrefresh.common.args;

import java.lang.annotation.*;

/**
 * @author baB_hyf
 * @date 2022/06/18
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Argument {

    String[] value();

    int argc() default 0;

}
