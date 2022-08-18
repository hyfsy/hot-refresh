package com.hyf.hotrefresh.plugin.arthas;

import com.hyf.hotrefresh.client.api.plugin.Plugin;
import com.hyf.hotrefresh.common.util.FastReflectionUtils;
import com.hyf.hotrefresh.common.util.ReflectionUtils;
import com.hyf.hotrefresh.core.util.Util;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * @author baB_hyf
 * @date 2022/06/26
 */
public class ArthasBootstrapPlugin implements Plugin {

    @Override
    public void setup() throws Exception {
        String className = "com.taobao.arthas.boot.Bootstrap";
        Class<?> clazz = ReflectionUtils.forName(className, Util.getInfrastructureJarClassLoader());
        FastReflectionUtils.fastInvokeMethod(clazz, "main", new Class[]{String[].class}, (Object) null);

        PrintStream out = System.out;
        InputStream in = System.in;
        PrintStream err = System.err;
    }
}
