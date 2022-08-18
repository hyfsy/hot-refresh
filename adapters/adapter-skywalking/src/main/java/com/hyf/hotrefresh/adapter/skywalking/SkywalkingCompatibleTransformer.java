package com.hyf.hotrefresh.adapter.skywalking;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author baB_hyf
 * @date 2022/07/05
 */
public class SkywalkingCompatibleTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        // modify the skywalking transformed class bytes

        // TODO

        return null;
    }
}
