package com.hyf.hotrefresh.agent;

import com.hyf.hotrefresh.InfrastructureJarClassLoader;
import com.hyf.hotrefresh.Util;

import java.io.File;
import java.lang.reflect.*;

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

        Class<?> AttachmentProviderClass = cl.forName("net.bytebuddy.agent.ByteBuddyAgent$AttachmentProvider");
        Field DEFAULT = cl.getField(AttachmentProviderClass, "DEFAULT");


        Class<?> SimpleClass = cl.forName("net.bytebuddy.agent.ByteBuddyAgent$AttachmentProvider$Accessor$Simple");
        Method of = cl.getMethod(SimpleClass, "of", ClassLoader.class, File[].class);


        Class<?> UnavailableClass = cl.forName("net.bytebuddy.agent.ByteBuddyAgent$AttachmentProvider$Accessor$Unavailable");
        Field instance = cl.getField(UnavailableClass, "INSTANCE");

        Class<?> CompoundClass = cl.forName("net.bytebuddy.agent.ByteBuddyAgent$AttachmentProvider$Compound");

        Class<?> AttachmentProviderArrayClass = cl.forName("[Lnet.bytebuddy.agent.ByteBuddyAgent$AttachmentProvider;");

        Object o = Proxy.newProxyInstance(AttachmentProviderClass.getClassLoader(), new Class[]{AttachmentProviderClass}, (proxy, method, args) -> {
            if ("attempt".equals(method.getName())) {
                ToolsJarProcessor processor = new ToolsJarProcessor();
                String toolsJarPath = processor.getToolsJarPath();
                File toolsJar = new File(toolsJarPath);
                return toolsJar.isFile() && toolsJar.canRead()
                        ? cl.invokeMethod(of, null, cl, new File[]{toolsJar})
                        : cl.invokeField(instance, null);
            }
            return method.invoke(DEFAULT, args);
        });

        try {
            Object arr = Array.newInstance(AttachmentProviderClass, 2);
            Array.set(arr, 0, cl.invokeField(DEFAULT, null));
            Array.set(arr, 1, o);
            Constructor<?> CompoundConstructor = CompoundClass.getConstructor(AttachmentProviderArrayClass);
            return CompoundConstructor.newInstance(arr);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("Failed to get ext attachmentProvider instance");
        }
    }
}
