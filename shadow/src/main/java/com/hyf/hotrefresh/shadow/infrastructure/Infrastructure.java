package com.hyf.hotrefresh.shadow.infrastructure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation annotated class not recommend direct to use, please refer to the following template:
 * <pre>{@code
 * Class<?> clazz = InfraUtils.forName(InstrumentationHolder.class.getName());
 * return FastReflectionUtils.fastInvokeMethod(clazz, "getSystemStartProcessInstrumentation");
 * }</pre>
 *
 * @author baB_hyf
 * @date 2022/07/05
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Infrastructure {
}
