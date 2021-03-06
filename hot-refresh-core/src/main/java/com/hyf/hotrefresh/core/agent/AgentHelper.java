package com.hyf.hotrefresh.core.agent;

import com.hyf.hotrefresh.common.util.FastReflectionUtils;
import com.hyf.hotrefresh.common.util.ReflectionUtils;
import com.hyf.hotrefresh.core.classloader.InfrastructureJarClassLoader;
import com.hyf.hotrefresh.core.util.InfraUtils;
import com.hyf.hotrefresh.core.util.Util;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author baB_hyf
 * @date 2021/12/12
 */
public class AgentHelper {

    public static Object getAttachmentProvider() {

        // return new net.bytebuddy.agent.ByteBuddyAgent.AttachmentProvider.Compound(net.bytebuddy.agent.ByteBuddyAgent.AttachmentProvider.DEFAULT, () -> {
        //     ToolsJarProcessor processor = new ToolsJarProcessor();
        //     String toolsJarPath = processor.getToolsJarPath();
        //     File toolsJar = new File(toolsJarPath);
        //     return toolsJar.isFile() && toolsJar.canRead()
        //             ? net.bytebuddy.agent.ByteBuddyAgent.AttachmentProvider.Accessor.Simple.of(Util.getInfrastructureJarClassLoader(), toolsJar)
        //             : net.bytebuddy.agent.ByteBuddyAgent.AttachmentProvider.Accessor.Unavailable.INSTANCE;
        // });

        InfrastructureJarClassLoader cl = Util.getInfrastructureJarClassLoader();

        Class<?> AttachmentProviderClass = InfraUtils.forName("net.bytebuddy.agent.ByteBuddyAgent$AttachmentProvider");
        Field DEFAULT = ReflectionUtils.getField(AttachmentProviderClass, "DEFAULT");


        Class<?> SimpleClass = InfraUtils.forName("net.bytebuddy.agent.ByteBuddyAgent$AttachmentProvider$Accessor$Simple");
        Method of = ReflectionUtils.getMethod(SimpleClass, "of", ClassLoader.class, File[].class);


        Class<?> UnavailableClass = InfraUtils.forName("net.bytebuddy.agent.ByteBuddyAgent$AttachmentProvider$Accessor$Unavailable");
        Field instance = ReflectionUtils.getField(UnavailableClass, "INSTANCE");

        Class<?> CompoundClass = InfraUtils.forName("net.bytebuddy.agent.ByteBuddyAgent$AttachmentProvider$Compound");

        Class<?> AttachmentProviderArrayClass = InfraUtils.forName("[Lnet.bytebuddy.agent.ByteBuddyAgent$AttachmentProvider;");

        Object o = Proxy.newProxyInstance(AttachmentProviderClass.getClassLoader(), new Class[]{AttachmentProviderClass}, (proxy, method, args) -> {
            if ("attempt".equals(method.getName())) {
                ToolsJarProcessor processor = new ToolsJarProcessor();
                String toolsJarPath = processor.getToolsJarPath();
                File toolsJar = new File(toolsJarPath);
                return toolsJar.isFile() && toolsJar.canRead()
                        ? ReflectionUtils.invokeMethod(of, null, cl, new File[]{toolsJar})
                        : ReflectionUtils.invokeFieldGet(instance, null);
            }
            return method.invoke(DEFAULT, args);
        });

        try {
            Object arr = Array.newInstance(AttachmentProviderClass, 2);
            Array.set(arr, 0, ReflectionUtils.invokeFieldGet(DEFAULT, null));
            Array.set(arr, 1, o);
            Constructor<?> CompoundConstructor = CompoundClass.getConstructor(AttachmentProviderArrayClass);
            return CompoundConstructor.newInstance(arr);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("Failed to get ext attachmentProvider instance");
        }
    }

    public static void installSpringLoaded(Instrumentation instrumentation) {

        // TODO SpringLoaded is not compatible
        // InfrastructureJarClassLoader cl = Util.getInfrastructureJarClassLoader();
        //
        // Class<?> SpringLoadedAgentClass = cl.forName("org.springsource.loaded.agent.SpringLoadedAgent");
        //
        // Method premainMethod = ReflectUtils.getMethod(SpringLoadedAgentClass, "premain", String.class, Instrumentation.class);
        //
        // cl.invokeMethod(premainMethod, null, null, instrumentation);
    }

    public static synchronized void setTransformerToFirst(Instrumentation instrumentation, ClassFileTransformer classFileTransformer, boolean canRetransform) {
        try {
            Object transformerManager;
            if (canRetransform) {
                transformerManager = FastReflectionUtils.fastGetField(instrumentation, "mRetransfomableTransformerManager");
            }
            else {
                transformerManager = FastReflectionUtils.fastGetField(instrumentation, "mTransformerManager");
            }

            if (transformerManager == null) {
                instrumentation.addTransformer(classFileTransformer, canRetransform);
                return;
            }

            Object[] snapshotTransformerList = FastReflectionUtils.fastInvokeMethod(transformerManager, "getSnapshotTransformerList");
            if (snapshotTransformerList.length <= 0) {
                instrumentation.addTransformer(classFileTransformer, canRetransform);
                return;
            }

            // reflect set to first

            Class<?> TransformerManager$TransformerInfoClass = snapshotTransformerList[0].getClass();
            Object customTransformerInfo = ReflectionUtils.newClassInstance(TransformerManager$TransformerInfoClass,
                    new Class[]{transformerManager.getClass() /* inner class must has this extra param */, ClassFileTransformer.class},
                    transformerManager, classFileTransformer);
            List<Object> transformerInfos = new ArrayList<>();
            transformerInfos.add(customTransformerInfo);
            transformerInfos.addAll(Arrays.asList(snapshotTransformerList));
            Object mTransformerList = Array.newInstance(TransformerManager$TransformerInfoClass, transformerInfos.size());
            for (int i = 0; i < transformerInfos.size(); i++) {
                Array.set(mTransformerList, i, transformerInfos.get(i));
            }
            FastReflectionUtils.fastSetField(transformerManager, "mTransformerList", mTransformerList);

        } catch (Throwable t) {
            throw new RuntimeException("Failed to add transformer to first", t);
        }
    }
}
