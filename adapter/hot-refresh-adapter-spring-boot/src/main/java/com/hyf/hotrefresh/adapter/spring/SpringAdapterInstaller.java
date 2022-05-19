package com.hyf.hotrefresh.adapter.spring;

import com.hyf.hotrefresh.core.install.Installer;

/**
 * @author baB_hyf
 * @date 2022/05/12
 */
public class SpringAdapterInstaller implements Installer {

    @Override
    public void install() {
        System.out.println("spring installed");
    }
}
