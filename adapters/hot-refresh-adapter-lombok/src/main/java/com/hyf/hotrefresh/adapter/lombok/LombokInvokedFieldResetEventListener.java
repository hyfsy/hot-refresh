package com.hyf.hotrefresh.adapter.lombok;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.util.ReflectionUtils;
import com.hyf.hotrefresh.core.event.ByteCodeRefreshedEvent;
import com.hyf.hotrefresh.core.event.HotRefreshListener;
import com.hyf.hotrefresh.core.util.InfraUtils;

import java.lang.reflect.Field;

/**
 * @author baB_hyf
 * @date 2022/06/25
 */
public class LombokInvokedFieldResetEventListener implements HotRefreshListener<ByteCodeRefreshedEvent> {

    private static volatile Field lombokInvokedField;

    private static void resetLombokInvokedField() {
        if (lombokInvokedField == null) {
            try {
                Class<?> AstModificationNotifierDataClass = InfraUtils.forName("lombok.launch.AnnotationProcessorHider$AstModificationNotifierData");
                lombokInvokedField = ReflectionUtils.getField(AstModificationNotifierDataClass, "lombokInvoked");
            } catch (Exception e) {
                Log.error("Failed to get lombok invoked field", e);
            }
        }

        // compile each round
        ReflectionUtils.invokeFieldSet(lombokInvokedField, null, false);
    }

    @Override
    public void onRefreshEvent(ByteCodeRefreshedEvent event) {
        resetLombokInvokedField();
    }
}
