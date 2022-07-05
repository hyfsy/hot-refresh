/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hyf.hotrefresh.adapter.skywalking;

import com.hyf.hotrefresh.common.Log;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.apache.skywalking.apm.agent.core.boot.AgentPackageNotFoundException;
import org.apache.skywalking.apm.agent.core.conf.Config;
import org.apache.skywalking.apm.agent.core.conf.SnifferConfigInitializer;
import org.apache.skywalking.apm.agent.core.plugin.*;
import org.apache.skywalking.apm.agent.core.plugin.bootstrap.BootstrapInstrumentBoost;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.jdk9module.JDK9ModuleExporter;

import java.lang.instrument.Instrumentation;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * The main entrance of sky-walking agent, based on javaagent mechanism.
 */
public class AdaptedSkyWalkingAgent {

    public static void injectToInstrument(Instrumentation instrumentation) {
        new AdaptedSkyWalkingAgent().premainLevelup(null, instrumentation);
    }

    private static ElementMatcher.Junction<NamedElement> allAdaptedSkyWalkingAgentExcludeToolkit() {
        return ElementMatchers.nameStartsWith("org.apache.skywalking.").and(ElementMatchers.not(ElementMatchers.nameStartsWith("org.apache.skywalking.apm.toolkit.")));
    }

    public void premainLevelup(String agentArgs, Instrumentation instrumentation) throws PluginException {
        final PluginFinder pluginFinder;
        // try {
        //     // 初始化配置
        //     SnifferConfigInitializer.initializeCoreConfig(agentArgs);
        // } catch (Exception e) {
        //     // try to resolve a new logger, and use the new logger to write the error log here
        //     // LogManager.getLogger(AdaptedSkyWalkingAgent.class)
        //     //         .error(e, "SkyWalking agent initialized failure. Shutting down.");
        //     return;
        // } finally {
        //     // refresh logger again after initialization finishes
        // }

        // 加载所有def插件
        try {
            pluginFinder = new PluginFinder(new PluginBootstrap().loadPlugins());
        } catch (AgentPackageNotFoundException ape) {
            Log.error("Locate agent.jar failure. Shutting down.", ape);
            return;
        } catch (Exception e) {
            Log.error("SkyWalking agent initialized failure. Shutting down.", e);
            return;
        }

        final ByteBuddy byteBuddy = new ByteBuddy().with(TypeValidation.of(Config.Agent.IS_OPEN_DEBUGGING_CLASS));

        AgentBuilder agentBuilder = new AgentBuilder.Default(byteBuddy).ignore(
                ElementMatchers.nameStartsWith("net.bytebuddy.")
                        .or(ElementMatchers.nameStartsWith("org.slf4j."))
                        .or(ElementMatchers.nameStartsWith("org.groovy."))
                        .or(ElementMatchers.nameContains("javassist"))
                        .or(ElementMatchers.nameContains(".asm."))
                        .or(ElementMatchers.nameContains(".reflectasm."))
                        .or(ElementMatchers.nameStartsWith("sun.reflect"))
                        .or(allAdaptedSkyWalkingAgentExcludeToolkit())
                        .or(ElementMatchers.isSynthetic()));

        JDK9ModuleExporter.EdgeClasses edgeClasses = new JDK9ModuleExporter.EdgeClasses();
        try {
            agentBuilder = BootstrapInstrumentBoost.inject(pluginFinder, instrumentation, agentBuilder, edgeClasses);
        } catch (Exception e) {
            Log.error("SkyWalking agent inject bootstrap instrumentation failure. Shutting down.", e);
            return;
        }

        try {
            agentBuilder = JDK9ModuleExporter.openReadEdge(instrumentation, agentBuilder, edgeClasses);
        } catch (Exception e) {
            Log.error("SkyWalking agent open read edge in JDK 9+ failure. Shutting down.", e);
            return;
        }

        // 所有连接点
        agentBuilder.type(pluginFinder.buildMatch())
                // 实际的类增强处理对象
                .transform(new Transformer(pluginFinder))
                // 类重新装载策略
                // .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                // 添加日志输出
                // .with(new RedefinitionListener())
                // // 添加日志输出
                // .with(new Listener())
                // Instrumentation JVMTI JVM Tool Interface
                .installOn(instrumentation);
    }

    // /**
    //  * Main entrance. Use byte-buddy transform to enhance all classes, which define in plugins.
    //  */
    // public static void premain(String agentArgs, Instrumentation instrumentation) throws PluginException {
    //     final PluginFinder pluginFinder;
    //     try {
    //         // 初始化配置
    //         SnifferConfigInitializer.initializeCoreConfig(agentArgs);
    //     } catch (Exception e) {
    //         // try to resolve a new logger, and use the new logger to write the error log here
    //         LogManager.getLogger(AdaptedSkyWalkingAgent.class)
    //                 .error(e, "SkyWalking agent initialized failure. Shutting down.");
    //         return;
    //     } finally {
    //         // refresh logger again after initialization finishes
    //     }
    //
    //     // 加载所有def插件
    //     try {
    //         pluginFinder = new PluginFinder(new PluginBootstrap().loadPlugins());
    //     } catch (AgentPackageNotFoundException ape) {
    //         LOGGER.error(ape, "Locate agent.jar failure. Shutting down.");
    //         return;
    //     } catch (Exception e) {
    //         LOGGER.error(e, "SkyWalking agent initialized failure. Shutting down.");
    //         return;
    //     }
    //
    //     final ByteBuddy byteBuddy = new ByteBuddy().with(TypeValidation.of(Config.Agent.IS_OPEN_DEBUGGING_CLASS));
    //
    //     AgentBuilder agentBuilder = new AgentBuilder.Default(byteBuddy).ignore(
    //             nameStartsWith("net.bytebuddy.")
    //                     .or(nameStartsWith("org.slf4j."))
    //                     .or(nameStartsWith("org.groovy."))
    //                     .or(nameContains("javassist"))
    //                     .or(nameContains(".asm."))
    //                     .or(nameContains(".reflectasm."))
    //                     .or(nameStartsWith("sun.reflect"))
    //                     .or(allAdaptedSkyWalkingAgentExcludeToolkit())
    //                     .or(ElementMatchers.isSynthetic()));
    //
    //     JDK9ModuleExporter.EdgeClasses edgeClasses = new JDK9ModuleExporter.EdgeClasses();
    //     try {
    //         agentBuilder = BootstrapInstrumentBoost.inject(pluginFinder, instrumentation, agentBuilder, edgeClasses);
    //     } catch (Exception e) {
    //         LOGGER.error(e, "SkyWalking agent inject bootstrap instrumentation failure. Shutting down.");
    //         return;
    //     }
    //
    //     try {
    //         agentBuilder = JDK9ModuleExporter.openReadEdge(instrumentation, agentBuilder, edgeClasses);
    //     } catch (Exception e) {
    //         LOGGER.error(e, "SkyWalking agent open read edge in JDK 9+ failure. Shutting down.");
    //         return;
    //     }
    //
    //     // 添加类增强缓存
    //     if (Config.Agent.IS_CACHE_ENHANCED_CLASS) {
    //         try {
    //             agentBuilder = agentBuilder.with(new CacheableTransformerDecorator(Config.Agent.CLASS_CACHE_MODE));
    //             LOGGER.info("SkyWalking agent class cache [{}] activated.", Config.Agent.CLASS_CACHE_MODE);
    //         } catch (Exception e) {
    //             LOGGER.error(e, "SkyWalking agent can't active class cache.");
    //         }
    //     }
    //
    //     // 所有连接点
    //     agentBuilder.type(pluginFinder.buildMatch())
    //             // 实际的类增强处理对象
    //                 .transform(new Transformer(pluginFinder))
    //             // 类重新装载策略
    //                 .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
    //             // 添加日志输出
    //                 .with(new RedefinitionListener())
    //             // 添加日志输出
    //                 .with(new Listener())
    //             // Instrumentation JVMTI JVM Tool Interface
    //                 .installOn(instrumentation);
    //
    //     try {
    //         ServiceManager.INSTANCE.boot();
    //     } catch (Exception e) {
    //         LOGGER.error(e, "Skywalking agent boot failure.");
    //     }
    //
    //     Runtime.getRuntime()
    //             .addShutdownHook(new Thread(ServiceManager.INSTANCE::shutdown, "skywalking service shutdown thread"));
    // }

    private static class Transformer implements AgentBuilder.Transformer {
        private PluginFinder pluginFinder;

        Transformer(PluginFinder pluginFinder) {
            this.pluginFinder = pluginFinder;
        }

        @Override
        public DynamicType.Builder<?> transform(final DynamicType.Builder<?> builder,
                                                final TypeDescription typeDescription,
                                                final ClassLoader classLoader,
                                                final JavaModule module) {
            // List<AbstractClassEnhancePluginDefine> pluginDefines = pluginFinder.find(typeDescription);
            //
            // FieldList<FieldDescription.InDefinedShape> declaredFields = typeDescription.getDeclaredFields();
            // for (FieldDescription.InDefinedShape declaredField : declaredFields) {
            //     if (ClassEnhancePluginDefine.CONTEXT_ATTR_NAME.equals(declaredField.getName())) {
            //         return builder;
            //     }
            // }
            //
            // if (pluginDefines.size() > 0) {
            //     DynamicType.Builder<?> newBuilder = builder;
            //     EnhanceContext context = new EnhanceContext();
            //     // 通过所有插件进行增强，生成新的 newBuilder
            //     for (AbstractClassEnhancePluginDefine define : pluginDefines) {
            //         DynamicType.Builder<?> possibleNewBuilder = define.define(
            //                 typeDescription, newBuilder, classLoader, context);
            //         if (possibleNewBuilder != null) {
            //             newBuilder = possibleNewBuilder;
            //         }
            //     }
            //     if (context.isEnhanced()) {
            //         if (Log.isDebugMode()) {
            //             Log.debug("Finish the prepare stage for " + typeDescription.getName());
            //         }
            //     }
            //
            //     return newBuilder;
            // }
            //
            // if (Log.isDebugMode()) {
            //     Log.debug("Matched class " + typeDescription.getTypeName() + ", but ignore by finding mechanism.");
            // }
            return builder;
        }
    }

    // private static class Listener implements AgentBuilder.Listener {
    //     @Override
    //     public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
    //
    //     }
    //
    //     @Override
    //     public void onTransformation(final TypeDescription typeDescription,
    //                                  final ClassLoader classLoader,
    //                                  final JavaModule module,
    //                                  final boolean loaded,
    //                                  final DynamicType dynamicType) {
    //         if (Log.isDebugMode()) {
    //             Log.debug("On Transformation class " + typeDescription.getName());
    //         }
    //
    //         InstrumentDebuggingClass.INSTANCE.log(dynamicType);
    //     }
    //
    //     @Override
    //     public void onIgnored(final TypeDescription typeDescription,
    //                           final ClassLoader classLoader,
    //                           final JavaModule module,
    //                           final boolean loaded) {
    //
    //     }
    //
    //     @Override
    //     public void onError(final String typeName,
    //                         final ClassLoader classLoader,
    //                         final JavaModule module,
    //                         final boolean loaded,
    //                         final Throwable throwable) {
    //         Log.error("Enhance class " + typeName + " error.", throwable);
    //     }
    //
    //     @Override
    //     public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
    //     }
    // }
    //
    // private static class RedefinitionListener implements AgentBuilder.RedefinitionStrategy.Listener {
    //
    //     @Override
    //     public void onBatch(int index, List<Class<?>> batch, List<Class<?>> types) {
    //         /* do nothing */
    //     }
    //
    //     @Override
    //     public Iterable<? extends List<Class<?>>> onError(int index, List<Class<?>> batch, Throwable throwable, List<Class<?>> types) {
    //         Log.error("index=" + index + ", batch=" + batch + ", types=" + types, throwable);
    //         return Collections.emptyList();
    //     }
    //
    //     @Override
    //     public void onComplete(int amount, List<Class<?>> types, Map<List<Class<?>>, Throwable> failures) {
    //         /* do nothing */
    //     }
    // }
}
