package com.hyf.hotrefresh.plugin.execute;

import com.hyf.hotrefresh.core.classloader.ExtendClassLoader;
import com.hyf.hotrefresh.core.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/17
 */
public class ExecutableClassLoader extends ExtendClassLoader {

    private final Map<String, byte[]> executables = new HashMap<>(1);

    public ExecutableClassLoader() {
        super(Util.getThrowawayHotRefreshClassLoader()); // 使用 mcl 的类库
    }

    @Override
    protected Class<?> brokenLoadClass(String name) throws ClassNotFoundException {

        byte[] bytes = executables.get(name);
        if (bytes != null) {
            return defineClass(name, bytes, 0, bytes.length);
        }

        return super.brokenLoadClass(name);
    }

    public void addExecutable(String className, byte[] bytes) {
        executables.put(className, bytes);
    }

    public Map<String, byte[]> getExecutables() {
        return executables;
    }
}
