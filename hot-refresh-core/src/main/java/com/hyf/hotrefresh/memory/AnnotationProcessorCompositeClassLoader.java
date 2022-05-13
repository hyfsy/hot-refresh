package com.hyf.hotrefresh.memory;

import com.hyf.hotrefresh.classloader.CompositeClassLoader;
import com.hyf.hotrefresh.util.Util;

/**
 * 注解处理器的类加载器，不同类库可能使用不同的类加载器加载
 *
 * @author baB_hyf
 * @date 2022/05/13
 */
public class AnnotationProcessorCompositeClassLoader extends CompositeClassLoader {

    private static final AnnotationProcessorCompositeClassLoader INSTANCE =
            new AnnotationProcessorCompositeClassLoader(Util.getInfrastructureJarClassLoader());

    public AnnotationProcessorCompositeClassLoader(ClassLoader parent) {
        super(parent);
    }

    public static AnnotationProcessorCompositeClassLoader getInstance() {
        return INSTANCE;
    }
}
