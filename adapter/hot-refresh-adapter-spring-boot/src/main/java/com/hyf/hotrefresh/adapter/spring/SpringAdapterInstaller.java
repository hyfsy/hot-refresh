package com.hyf.hotrefresh.adapter.spring;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.core.install.Installer;

/**
 * @author baB_hyf
 * @date 2022/05/12
 */
public class SpringAdapterInstaller implements Installer {

    @Override
    public void install() {
        if (Log.isDebugMode()) {
            Log.debug("spring adapter installed");
        }
    }
}
