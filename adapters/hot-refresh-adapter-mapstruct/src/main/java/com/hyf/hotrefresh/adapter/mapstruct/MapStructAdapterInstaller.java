package com.hyf.hotrefresh.adapter.mapstruct;

import com.hyf.hotrefresh.core.install.Installer;
import com.hyf.hotrefresh.core.util.InfrastructureJarClassLoader;

/**
 * @author baB_hyf
 * @date 2022/05/12
 */
public class MapStructAdapterInstaller implements Installer {

    private static final String MAP_STRUCT_LOCAL_PATH           = "lib/mapstruct-1.4.1.Final.jar";
    private static final String MAP_STRUCT_PROCESSOR_LOCAL_PATH = "lib/mapstruct-processor-1.4.1.Final.jar";

    @Override
    public void install() {
        InfrastructureJarClassLoader.getInstance().registerInfrastructureJar("mapstruct", MAP_STRUCT_LOCAL_PATH);
        InfrastructureJarClassLoader.getInstance().registerInfrastructureJar("mapstruct-processor", MAP_STRUCT_PROCESSOR_LOCAL_PATH);
    }
}
