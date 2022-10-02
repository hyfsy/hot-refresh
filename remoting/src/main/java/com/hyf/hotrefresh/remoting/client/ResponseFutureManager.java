package com.hyf.hotrefresh.remoting.client;

import com.hyf.hotrefresh.remoting.ResponseFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author baB_hyf
 * @date 2022/10/01
 */
public class ResponseFutureManager {

    private final Map<Integer, ResponseFuture> futureTables = new ConcurrentHashMap<>();

    public void put(Integer id, ResponseFuture responseFuture) {
        futureTables.put(id, responseFuture);
    }

    public ResponseFuture get(Integer id) {
        return futureTables.get(id);
    }

    public ResponseFuture remove(Integer id) {
        return futureTables.remove(id);
    }

    public Map<Integer, ResponseFuture> getFutureTables() {
        return futureTables;
    }
}
