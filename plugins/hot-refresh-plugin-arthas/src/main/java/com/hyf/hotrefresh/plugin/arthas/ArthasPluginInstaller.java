package com.hyf.hotrefresh.plugin.arthas;

import com.hyf.hotrefresh.common.util.ReflectionUtils;
import com.hyf.hotrefresh.core.install.Installer;
import com.hyf.hotrefresh.core.util.Util;

/**
 * @author baB_hyf
 * @date 2022/06/26
 */
public class ArthasPluginInstaller implements Installer {

    @Override
    public void install() {

        if (ArthasUtils.existArthasConfiguration()) {
            return;
        }

        // Map<String, String> params = new HashMap<>();
        // ArthasAgent.attach(params);
        try {
            Class<?> arthasAgentClass = ReflectionUtils.forName("com.taobao.arthas.agent.attach.ArthasAgent",
                Util.getInfrastructureJarClassLoader());
            ReflectionUtils.getMethod(arthasAgentClass, "attach");
        } catch (Exception ignored) {
        }
    }
}
