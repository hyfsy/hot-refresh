package com.hyf.hotrefresh.adapter.skywalking;

import com.hyf.hotrefresh.core.refresh.HotRefreshTransformPostProcessor;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class SkywalkingTransformPostProcessor implements HotRefreshTransformPostProcessor {

    private static final boolean skywalkingPresent;
    private static final boolean agentCacheEnhancedClassEnabled;

    static {
        skywalkingPresent = SkywalkingHelper.skywalkingAgentPresent();
        agentCacheEnhancedClassEnabled = skywalkingPresent && SkywalkingHelper.agentCacheEnhancedClassEnabled();
    }

    @Override
    public byte[] postTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer, byte[] memoryClassfileBuffer) throws IllegalClassFormatException {

        if (!skywalkingPresent) {
            return null;
        }

        // not transform
        if (classBeingRedefined == null) {
            return null;
        }

        // not skywalking enhanced class
        if (SkywalkingHelper.enhancedBySkywalking(classBeingRedefined)) {

            if (!agentCacheEnhancedClassEnabled) {
                // // TODO 通过class拿不到retransform后的字节码
                // try {
                //     classfileBuffer = SkywalkingHelper.parseBytes(loader, className, classBeingRedefined, protectionDomain);
                // } catch (Throwable t) {
                //     Log.error("SkywalkingHelper parseBytes invoke failed", t);
                //     return null;
                // }
                return null;
            }

            return SkywalkingHelper.amendClassBytes(classBeingRedefined, classfileBuffer, memoryClassfileBuffer);
        }


        return null;
    }
}
