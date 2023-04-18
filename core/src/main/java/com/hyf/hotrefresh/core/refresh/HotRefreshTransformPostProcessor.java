package com.hyf.hotrefresh.core.refresh;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public interface HotRefreshTransformPostProcessor {

    byte[] postTransform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer, byte[] memoryClassfileBuffer) throws IllegalClassFormatException;

}
