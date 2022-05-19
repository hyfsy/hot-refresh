package com.hyf.hotrefresh.remoting.server;

import com.hyf.hotrefresh.remoting.exception.RpcException;
import com.hyf.hotrefresh.remoting.exception.ServerException;

import java.io.InputStream;
import java.io.OutputStream;

public interface RpcServer {

    void start() throws ServerException;

    void handle(InputStream is, OutputStream os) throws RpcException;

    void stop() throws ServerException;
}
