package com.hyf.hotrefresh.adapter.lombok;

import com.hyf.hotrefresh.classloader.DelegateClassLoader;

/**
 * @author baB_hyf
 * @date 2022/05/12
 */
@Deprecated
public class LombokShadowClassLoaderDelegate extends DelegateClassLoader {

    public LombokShadowClassLoaderDelegate(ClassLoader shadowClassLoader) {
        super(shadowClassLoader);
    }

    @Override
    protected boolean loadCondition(String name) {
        return name.startsWith("lombok");
    }
}
