package com.hyf.hotrefresh.adapter.spring.config;

import com.hyf.hotrefresh.core.install.CoreInstaller;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author baB_hyf
 * @date 2022/05/13
 */
public class HotRefreshEnableCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return CoreInstaller.enable();
    }
}
