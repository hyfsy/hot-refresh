package com.hyf.hotrefresh.adapter.skywalking;

import com.hyf.hotrefresh.core.install.Installer;
import com.hyf.hotrefresh.core.util.Util;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

/**
 * @author baB_hyf
 * @date 2022/07/02
 */
public class SkywalkingInstaller implements Installer {

    public static final ClassFileTransformer skywalkingCompatibleTransformer = new SkywalkingCompatibleTransformer();

    @Override
    public void install() {
        Instrumentation instrumentation = Util.getInstrumentation();
        instrumentation.addTransformer(skywalkingCompatibleTransformer, true);
    }

}
