package com.hyf.hotrefresh.plugin.arthas;

import com.hyf.hotrefresh.core.install.Installer;
import com.taobao.arthas.agent.attach.ArthasAgent;

import java.util.HashMap;
import java.util.Map;

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
        ArthasAgent.attach();
    }
}
