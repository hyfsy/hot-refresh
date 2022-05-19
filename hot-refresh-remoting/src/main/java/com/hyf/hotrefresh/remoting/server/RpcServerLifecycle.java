package com.hyf.hotrefresh.remoting.server;

import com.hyf.hotrefresh.remoting.exception.ServerException;

/**
 * @author baB_hyf
 * @date 2022/05/19
 */
public interface RpcServerLifecycle {

    void start() throws ServerException;

    void stop() throws ServerException;
}
