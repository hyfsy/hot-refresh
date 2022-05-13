package com.hyf.hotrefresh.adapter.lombok;

import com.hyf.hotrefresh.install.CoreInstaller;

/**
 * @author baB_hyf
 * @date 2022/05/13
 */
public class LombokInstallTests {

    public static void main(String[] args) throws Exception {
        boolean install = CoreInstaller.install();

        System.out.println(new LombokAdapterInstaller().getShadowClassLoader().getParent());
    }
}
