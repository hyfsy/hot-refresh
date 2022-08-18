/*
 * Copyright 2013-2022 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package com.hyf.hotrefresh.adapter.spring.copy;

import com.hyf.hotrefresh.adapter.spring.ApplicationContextUtils;
import com.hyf.hotrefresh.adapter.spring.copy.caches.AutowiredCommonAnnotationCaches;
import com.hyf.hotrefresh.adapter.spring.copy.caches.ResetBeanPostProcessorCaches;
import com.hyf.hotrefresh.adapter.spring.copy.caches.ResetRequestMappingCaches;
import com.hyf.hotrefresh.adapter.spring.copy.caches.ResetSpringStaticCaches;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.core.util.InfraUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import static com.hyf.hotrefresh.common.util.FastReflectionUtils.*;

/**
 * Registers
 *
 * @author Jiri Bubnik
 * @author baB_hyf
 * @date 2022/05/27
 */
public class SpringAgent {

    public static final String PACKAGE_PLACEHOLDER = "com.hyf.hotrefresh";

    /**
     * Flag to check reload status.
     * In unit test we need to wait for reload finish before the test can continue. Set flag to true
     * in the test class and wait until the flag is false again.
     */
    public static boolean reloadFlag = false;

    private static Map<ClassPathBeanDefinitionScanner, SpringAgent> instances = new HashMap<>();

    static {
        ApplicationContext context = ApplicationContextUtils.getApplicationContext();
        if (context instanceof BeanDefinitionRegistry) {
            SpringAgent instance = getInstance(new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) context));
            instance.registerBasePackage(PACKAGE_PLACEHOLDER);
        }
    }

    // target scanner this agent shadows
    ClassPathBeanDefinitionScanner scanner;

    // list of basePackages registered with target scanner
    Set<String> basePackages = new HashSet<>();

    // registry obtained from the scanner
    BeanDefinitionRegistry registry;

    // metadata resolver obtained from the scanner
    ScopeMetadataResolver scopeMetadataResolver;

    // bean name generator obtained from the scanner
    BeanNameGenerator beanNameGenerator;

    // Create new instance from getInstance(ClassPathBeanDefinitionScanner scanner) and obtain services from the scanner
    private SpringAgent(ClassPathBeanDefinitionScanner scanner) {
        this.scanner = scanner;
        this.registry = scanner.getRegistry();
        this.scopeMetadataResolver = fastGetField(scanner, scanner.getClass(), "scopeMetadataResolver");
        this.beanNameGenerator = fastGetField(scanner, scanner.getClass(), "beanNameGenerator");
    }

    /**
     * Return an agent instance for a scanner. If the instance does not exists yet, it is created.
     *
     * @param scanner the scanner
     * @return agent instance
     */
    public static SpringAgent getInstance(ClassPathBeanDefinitionScanner scanner) {
        SpringAgent classPathBeanDefinitionScannerAgent = instances.get(scanner);
        // registry may be different if there is multiple app. (this is just a temporary solution)
        if (classPathBeanDefinitionScannerAgent == null || classPathBeanDefinitionScannerAgent.registry != scanner.getRegistry()) {
            instances.put(scanner, new SpringAgent(scanner));
        }
        return instances.get(scanner);
    }

    /**
     * Find scanner agent by base package.
     *
     * @param basePackage the scanner agent or null if no such agent exists
     * @return the agent
     */
    public static SpringAgent getInstance(String basePackage) {
        for (SpringAgent scannerAgent : instances.values()) {
            if (scannerAgent.basePackages.contains(basePackage)) {
                return scannerAgent;
            }
        }
        return null;
    }

    public static void refreshClass(String name, byte[] classDefinition) throws Throwable {

        // use app class loader get class to avoid cannot find the class during DI
        Class<?> clazz = InfraUtils.forName(name);

        refreshClass(PACKAGE_PLACEHOLDER, classDefinition, clazz);
    }

    /**
     * Called by a reflection command from SpringPlugin transformer.
     *
     * @param basePackage     base package on witch the transformer was registered, used to obtain associated scanner.
     * @param classDefinition new class definition
     * @throws IOException error working with classDefinition
     */
    public static void refreshClass(String basePackage, byte[] classDefinition, Class<?> clazz) throws Throwable {

        // multi instance
        // 字节码植入构造器进行收集
        // org.springframework.core.LocalVariableTableParameterNameDiscoverer#parameterNamesCache

        // @Configuration相关功能

        ResetSpringStaticCaches.reset();

        SpringAgent scannerAgent = getInstance(basePackage);
        if (scannerAgent == null) {
            Log.warn("basePackage '{}' not associated with any scannerAgent: " + basePackage);
            return;
        }

        BeanDefinition beanDefinition = scannerAgent.resolveBeanDefinition(classDefinition);
        if (beanDefinition instanceof AbstractBeanDefinition) {
            ((AbstractBeanDefinition) beanDefinition).setBeanClass(clazz);
        }
        if (beanDefinition != null) {
            scannerAgent.defineBean(beanDefinition);
        }

        reloadFlag = false;
    }

    public void registerBasePackage(String basePackage) {
        this.basePackages.add(basePackage);

        // PluginManagerInvoker.callPluginMethod(SpringPlugin.class, getClass().getClassLoader(),
        //         "registerComponentScanBasePackage", new Class[]{String.class}, new Object[]{basePackage});
    }


    /**
     * Resolve candidate to a bean definition and (re)load in Spring.
     * Synchronize to avoid parallel bean definition - usually on reload the beans are interrelated
     * and parallel load will cause concurrent modification exception.
     *
     * @param candidate the candidate to reload
     */
    public void defineBean(BeanDefinition candidate) {
        synchronized (getClass()) { // TODO sychronize on DefaultListableFactory.beanDefinitionMap?

            ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
            candidate.setScope(scopeMetadata.getScopeName());
            String beanName = this.beanNameGenerator.generateBeanName(candidate, registry);

            if (candidate instanceof AbstractBeanDefinition) {
                postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
            }
            if (candidate instanceof AnnotatedBeanDefinition) {
                processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
            }

            removeIfExists(beanName);
            if (checkCandidate(beanName, candidate)) {

                BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                definitionHolder = applyScopedProxyMode(scopeMetadata, definitionHolder, registry);

                if (Log.isDebugMode()) {
                    Log.debug("Registering Spring bean: " + beanName);
                    Log.debug("Bean definition: " + beanName);
                }
                registerBeanDefinition(definitionHolder, registry);

                DefaultListableBeanFactory bf = maybeRegistryToBeanFactory();
                if (bf != null) {
                    ResetRequestMappingCaches.reset(bf);
                }

                // ProxyReplacer.clearAllProxies();
                freezeConfiguration();
            }
        }
    }

    /**
     * If registry contains the bean, remove it first (destroying existing singletons).
     *
     * @param beanName name of the bean
     */
    private void removeIfExists(String beanName) {
        if (registry.containsBeanDefinition(beanName)) {
            if (Log.isDebugMode()) {
                Log.debug("Removing bean definition: " + beanName);
            }
            DefaultListableBeanFactory bf = maybeRegistryToBeanFactory();
            if (bf != null) {
                ResetRequestMappingCaches.reset(bf);
            }
            registry.removeBeanDefinition(beanName);

            ResetSpringStaticCaches.reset();
            if (bf != null) {
                ResetBeanPostProcessorCaches.reset(bf);
                AutowiredCommonAnnotationCaches.reset(bf, beanName);
            }
        }
    }

    private DefaultListableBeanFactory maybeRegistryToBeanFactory() {
        if (registry instanceof DefaultListableBeanFactory) {
            return (DefaultListableBeanFactory) registry;
        }
        else if (registry instanceof GenericApplicationContext) {
            return ((GenericApplicationContext) registry).getDefaultListableBeanFactory();
        }
        return null;
    }

    // rerun freez configuration - this method is enhanced with cache reset
    private void freezeConfiguration() {
        if (registry instanceof DefaultListableBeanFactory) {
            ((DefaultListableBeanFactory) registry).freezeConfiguration();
        }
        else if (registry instanceof GenericApplicationContext) {
            (((GenericApplicationContext) registry).getDefaultListableBeanFactory()).freezeConfiguration();
        }
    }

    /**
     * Resolve bean definition from class definition if applicable.
     *
     * @param bytes class definition.
     * @return the definition or null if not a spring bean
     *
     * @throws IOException
     */
    public BeanDefinition resolveBeanDefinition(byte[] bytes) throws IOException {
        Resource resource = new ByteArrayResource(bytes);
        resetCachingMetadataReaderFactoryCache();
        MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);

        if (isCandidateComponent(metadataReader)) {
            ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
            sbd.setResource(resource);
            sbd.setSource(resource);
            if (isCandidateComponent(sbd)) {
                if (Log.isDebugMode()) {
                    Log.debug("Identified candidate component class: " + metadataReader.getClassMetadata().getClassName());
                }
                return sbd;
            }
            else {
                if (Log.isDebugMode()) {
                    Log.debug("Ignored because not a concrete top-level class: " + metadataReader.getClassMetadata().getClassName());
                }
                return null;
            }
        }
        else {
            if (Log.isDebugMode()) {
                Log.debug("Ignored because not matching any filter: " + metadataReader.getClassMetadata().getClassName());
            }
            return null;
        }
    }

    private MetadataReaderFactory getMetadataReaderFactory() {

        Field metadataReaderFactoryField = ReflectionUtils.findField(scanner.getClass(), "metadataReaderFactory");
        metadataReaderFactoryField.setAccessible(true);
        return (MetadataReaderFactory) ReflectionUtils.getField(metadataReaderFactoryField, scanner);
    }

    // metadataReader contains cache of loaded classes, reset this cache before BeanDefinition is resolved
    private void resetCachingMetadataReaderFactoryCache() {
        if (getMetadataReaderFactory() instanceof CachingMetadataReaderFactory) {

            Optional.ofNullable(fastGetFieldNoException(getMetadataReaderFactory(), CachingMetadataReaderFactory.class, "metadataReaderCache")
                    .orElseGet(() -> fastGetFieldNoException(getMetadataReaderFactory(), CachingMetadataReaderFactory.class, "classReaderCache")
                            .orElse(null))).ifPresent(metadataReaderCache -> {
                ((Map) metadataReaderCache).clear();
                if (Log.isDebugMode()) {
                    Log.debug("Cache cleared: CachingMetadataReaderFactory.clearCache()");
                }
            });

        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////
    // Access private / protected members
    ////////////////////////////////////////////////////////////////////////////////////////////

    private BeanDefinitionHolder applyScopedProxyMode(
            ScopeMetadata metadata, BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {
        return fastInvokeMethod(AnnotationConfigUtils.class, "applyScopedProxyMode", new Class[]{ScopeMetadata.class, BeanDefinitionHolder.class, BeanDefinitionRegistry.class}, metadata, definition, registry);
    }

    private void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        fastInvokeMethod(scanner, ClassPathBeanDefinitionScanner.class, "registerBeanDefinition", new Class[]{BeanDefinitionHolder.class, BeanDefinitionRegistry.class}, definitionHolder, registry);
    }

    private boolean checkCandidate(String beanName, BeanDefinition candidate) {
        return fastInvokeMethod(scanner, ClassPathBeanDefinitionScanner.class, "checkCandidate", new Class[]{String.class, BeanDefinition.class}, beanName, candidate);
    }

    private void processCommonDefinitionAnnotations(AnnotatedBeanDefinition candidate) {
        fastInvokeMethod(AnnotationConfigUtils.class, "processCommonDefinitionAnnotations", new Class[]{AnnotatedBeanDefinition.class}, candidate);
    }

    private void postProcessBeanDefinition(AbstractBeanDefinition candidate, String beanName) {
        fastInvokeMethod(scanner, ClassPathBeanDefinitionScanner.class, "postProcessBeanDefinition", new Class[]{AbstractBeanDefinition.class, String.class}, candidate, beanName);
    }

    private boolean isCandidateComponent(AnnotatedBeanDefinition sbd) {
        return fastInvokeMethod(scanner, ClassPathScanningCandidateComponentProvider.class, "isCandidateComponent", new Class[]{AnnotatedBeanDefinition.class}, sbd);
    }

    private boolean isCandidateComponent(MetadataReader metadataReader) {
        return fastInvokeMethod(scanner, ClassPathScanningCandidateComponentProvider.class, "isCandidateComponent", new Class[]{MetadataReader.class}, metadataReader);
    }
}