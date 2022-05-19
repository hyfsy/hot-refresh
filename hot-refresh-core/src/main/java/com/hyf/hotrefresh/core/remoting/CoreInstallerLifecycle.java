package com.hyf.hotrefresh.core.remoting;

import com.hyf.hotrefresh.core.install.CoreInstaller;
import com.hyf.hotrefresh.remoting.exception.ServerException;
import com.hyf.hotrefresh.remoting.server.RpcServerLifecycle;

/**
 * @author baB_hyf
 * @date 2022/05/19
 */
public class CoreInstallerLifecycle implements RpcServerLifecycle {

    @Override
    public void start() throws ServerException {
        if (!CoreInstaller.install()) {
            throw new ServerException("Core install failed");
        }
    }

    @Override
    public void stop() throws ServerException {

    }
}
