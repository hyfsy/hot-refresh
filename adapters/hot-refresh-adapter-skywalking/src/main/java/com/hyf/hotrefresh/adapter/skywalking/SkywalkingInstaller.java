package com.hyf.hotrefresh.adapter.skywalking;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.util.ReflectionUtils;
import com.hyf.hotrefresh.core.install.Installer;
import com.hyf.hotrefresh.core.util.Util;

import java.lang.instrument.Instrumentation;

/**
 * @author baB_hyf
 * @date 2022/07/02
 */
public class SkywalkingInstaller implements Installer {

    @Override
    public void install() {
        // try {
        //
        //     Class.forName("org.apache.skywalking.apm.agent.SkyWalkingAgent");
        //     // local environment cannot work, must be an artifact
        //     // Instrumentation instrumentation = Util.getInstrumentation();
        //     Instrumentation instrumentation = InstrumentationHolder.getSystemStartProcessInstrumentation();
        //     AdaptedSkyWalkingAgent.injectToInstrument(instrumentation);
        //
        //
        // } catch (ClassNotFoundException ignored) {
        //     // not an skywalking agent environment
        // }
        // catch (Throwable t) {
        //     // not affect user application
        //     Log.error("Failed to install skywalking", t);
        // }

        // Class<?> AdaptedSkyWalkingAgentClass = InfraUtils.forName("com.hyf.hotrefresh.adapter.skywalking.AdaptedSkyWalkingAgent");
        // Method injectToInstrumentMethod = ReflectionUtils.getMethod(AdaptedSkyWalkingAgentClass, "injectToInstrument", Instrumentation.class);
        // ReflectionUtils.invokeMethod(injectToInstrumentMethod, null, Util.getInstrumentation());
    }
}
