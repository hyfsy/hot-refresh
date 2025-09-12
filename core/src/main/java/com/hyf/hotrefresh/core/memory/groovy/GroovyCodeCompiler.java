package com.hyf.hotrefresh.core.memory.groovy;

import com.hyf.hotrefresh.common.util.FastReflectionUtils;
import com.hyf.hotrefresh.core.util.InfraUtils;
import com.hyf.hotrefresh.core.util.Util;
import com.hyf.hotrefresh.shadow.infrastructure.Infrastructure;
import groovy.lang.GroovyClassLoader;

public class GroovyCodeCompiler {

    public static Class<?> compile(String content) {
        Class<?> klass = InfraUtils.forName(InnerGroovyCodeCompiler.class.getName());
        return FastReflectionUtils.fastInvokeMethod(klass, "compile", new Class[]{String.class}, content);
    }

    @Infrastructure
    private static class InnerGroovyCodeCompiler {

        private static final GroovyClassLoader INSTANCE = new GroovyClassLoader(Util.getHotRefreshClassLoader());

        public static Class<?> compile(String content) {
            content = content.replace("$", "\\$");
            return INSTANCE.parseClass(content);
        }
    }
}
