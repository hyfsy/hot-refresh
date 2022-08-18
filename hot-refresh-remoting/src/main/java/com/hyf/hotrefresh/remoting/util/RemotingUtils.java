package com.hyf.hotrefresh.remoting.util;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author baB_hyf
 * @date 2022/08/17
 */
public abstract class RemotingUtils {

    public static SocketAddress parseSocketAddress(String addr) {
        if (addr == null) {
            throw new IllegalArgumentException("addr illegal");
        }

        String[] addrPair = addr.split(":");
        if (addrPair.length != 2) {
            throw new IllegalArgumentException("addr illegal");
        }

        String host = addrPair[0];
        int port;
        try {
            port = Integer.parseInt(addrPair[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("addr illegal", e);
        }

        return new InetSocketAddress(host, port);
    }

    public static String parseAddress(SocketAddress address) {
        if (address == null) {
            return "";
        }

        String addr = address.toString();
        int idx = addr.lastIndexOf("/");
        if (idx != -1) {
            addr = addr.substring(idx + 1);
        }
        return addr;
    }
}
