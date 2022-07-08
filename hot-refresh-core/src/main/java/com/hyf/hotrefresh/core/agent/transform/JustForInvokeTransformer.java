package com.hyf.hotrefresh.core.agent.transform;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class JustForInvokeTransformer implements ClassFileTransformer {

    public static final JustForInvokeTransformer INSTANCE = new JustForInvokeTransformer();

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return null; // trigger collect and reset enhanced instrumentation
    }
}
