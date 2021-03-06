package com.hyf.hotrefresh.adapter.lombok;

import com.hyf.hotrefresh.core.install.Installer;
import com.hyf.hotrefresh.core.memory.AnnotationProcessorCompositeClassLoader;
import com.hyf.hotrefresh.core.classloader.InfrastructureJarClassLoader;
import com.hyf.hotrefresh.core.util.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author baB_hyf
 * @date 2022/05/12
 */
public class LombokAdapterInstaller implements Installer {

    public static final String LOMBOK_LOCAL_PATH = "lib/lombok-1.18.12.jar";

    @Override
    public void install() {

        // though lombok lib must ShadowClassLoader to load
        // but this action used to make sure some .class file in lombok lib can be load by infrastructure class loader
        InfrastructureJarClassLoader.getInstance().registerInfrastructureJar("lombok", LOMBOK_LOCAL_PATH);

        // when lombok lib and javac lib not loaded by the same class loader,
        // execute lombok annotation processor function will throw ClassNotDefError error
        ClassLoader shadowClassLoader = getShadowClassLoader();
        replaceParent(shadowClassLoader, Util.getInfrastructureJarClassLoader());
    }

    public ClassLoader getShadowClassLoader() {
        try {
            Class<?> mainClass = InfrastructureJarClassLoader.getInstance().loadClass("lombok.launch.Main");
            Method getShadowClassLoaderMethod = mainClass.getDeclaredMethod("getShadowClassLoader");
            getShadowClassLoaderMethod.setAccessible(true);
            return (ClassLoader) getShadowClassLoaderMethod.invoke(null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public ClassLoader replaceParent(ClassLoader source, ClassLoader parent) {
        try {
            Field parentField = ClassLoader.class.getDeclaredField("parent");
            parentField.setAccessible(true);
            ClassLoader oldClassLoader = (ClassLoader) parentField.get(source);
            parentField.set(source, parent);
            return oldClassLoader;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
