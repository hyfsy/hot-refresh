package com.hyf.hotrefresh.spring;

import com.hyf.hotrefresh.Log;
import com.hyf.hotrefresh.install.CoreInstaller;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author baB_hyf
 * @date 2022/05/13
 */
@Component
public class HotRefreshInstaller implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        if (!CoreInstaller.install()) {
            Log.warn("Hot refresh plugin install failed");
        }
    }
}
