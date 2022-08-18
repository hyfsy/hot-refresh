package com.hyf.hotrefresh.core.install;

import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.Services;
import com.hyf.hotrefresh.core.exception.InstallException;
import com.hyf.hotrefresh.core.util.Util;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author baB_hyf
 * @date 2022/05/12
 */
public class CoreInstaller {

    private static final boolean enable;

    private static final AtomicBoolean invoked = new AtomicBoolean(false);

    static {
        enable = install();
    }

    public static boolean enable() {
        return install();
    }

    public static boolean install() {
        if (invoked.compareAndSet(false, true)) {
            try {

                // check
                Util.getInstrumentation();

                new CoreInstaller().invokeInstaller();

                return true;
            } catch (Throwable e) {
                Log.error("Hot refresh plugin install failed", e);
                return false;
            }
        }

        return enable;
    }

    public void invokeInstaller() throws InstallException {
        try {
            List<Installer> installers = Services.gets(Installer.class);
            for (Installer installer : installers) {
                installer.install();
            }
        } catch (Throwable t) {
            throw new InstallException("Install failed", t);
        }
    }
}
