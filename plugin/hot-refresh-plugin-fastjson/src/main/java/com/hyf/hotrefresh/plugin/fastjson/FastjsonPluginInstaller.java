package com.hyf.hotrefresh.plugin.fastjson;

import com.hyf.hotrefresh.core.install.Installer;
import com.hyf.hotrefresh.core.util.Util;

/**
 * @author baB_hyf
 * @date 2022/05/18
 */
public class FastjsonPluginInstaller implements Installer {

    @Override
    public void install() {
        Util.getInfrastructureJarClassLoader().registerInfrastructureJar("fastjson", "lib/fastjson-1.2.76.jar");
    }
}
