package com.hyf.hotrefresh.adapter.mapstruct;

import com.hyf.hotrefresh.install.Installer;
import com.hyf.hotrefresh.memory.AnnotationProcessorCompositeClassLoader;
import com.hyf.hotrefresh.util.InfrastructureJarClassLoader;
import com.hyf.hotrefresh.util.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author baB_hyf
 * @date 2022/05/12
 */
public class MapStructAdapterInstaller implements Installer {

    private static final String MAPSTRUCT_LOCAL_PATH = "lib/mapstruct-1.4.1.Final.jar";
    private static final String MAPSTRUCT_PROCESSOR_LOCAL_PATH = "lib/mapstruct-processor-1.4.1.Final.jar";

    @Override
    public void install() {
        InfrastructureJarClassLoader.getInstance().registerInfrastructureJar("mapstruct", MAPSTRUCT_LOCAL_PATH);
        InfrastructureJarClassLoader.getInstance().registerInfrastructureJar("mapstruct-processor", MAPSTRUCT_PROCESSOR_LOCAL_PATH);
    }
}
