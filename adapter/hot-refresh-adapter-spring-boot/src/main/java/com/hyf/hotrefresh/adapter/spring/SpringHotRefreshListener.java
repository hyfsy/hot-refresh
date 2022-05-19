package com.hyf.hotrefresh.adapter.spring;

import com.hyf.hotrefresh.core.event.ByteCodeRefreshedEvent;
import com.hyf.hotrefresh.core.event.HotRefreshListener;
import com.hyf.hotrefresh.core.memory.MemoryClassLoader;
import com.hyf.hotrefresh.core.util.Util;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/19
 */
public class SpringHotRefreshListener implements HotRefreshListener<ByteCodeRefreshedEvent> {

    @Override
    public void onRefreshEvent(ByteCodeRefreshedEvent event) {
        System.out.println("received event");

        MemoryClassLoader memoryClassLoader = Util.getThrowawayMemoryClassLoader();

        Map<String, byte[]> compiledBytes = event.getCompiledBytes();
        compiledBytes.forEach((n, bs) -> {

            Class<?> clazz = memoryClassLoader.getClass(n);

            String[] beanNamesForType = ApplicationContextUtils.getBeanFactory().getBeanNamesForType(clazz);

            Component component = AnnotationUtils.findAnnotation(clazz, Component.class);
            if (component != null) {

            }

        });

        RequestMappingHandlerMapping mappingHandlerMapping = new RequestMappingHandlerMapping();
    }

    // org.springframework.util.ReflectionUtils.declaredMethodsCache

    // org.springframework.beans.CachedIntrospectionResults#classCache
    // org.springframework.beans.CachedIntrospectionResults#strongClassCache
    // org.springframework.beans.CachedIntrospectionResults#softClassCache

    // multi instance
    // 字节码植入构造器进行收集

    // org.springframework.core.LocalVariableTableParameterNameDiscoverer#parameterNamesCache

    // 4xx
    // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping#handlerMethods
    // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping#urlMap
    // 5xx
    // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping$MappingRegistry#registry
    // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping$MappingRegistry#mappingLookup
    // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping$MappingRegistry#urlLookup
    // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping$MappingRegistry#nameLookup
    // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping$MappingRegistry#corsLookup

    // org.springframework.web.servlet.handler.AbstractHandlerMethodMapping.initHandlerMethods

    // @Autowired/@Resource等依赖对象也得更改
    // Aware接口
    // inner @Bean
    // Conditional变为false，需要删除容器内的 ConditionEvaluator

    // org.springframework.core.annotation.AnnotationUtils#findAnnotationCache
    // org.springframework.core.annotation.AnnotationUtils#annotatedInterfaceCache

    // org.springframework.beans.factory.support.DefaultListableBeanFactory
    // resolvableDependencies
    // beanDefinitionMap
    // mergedBeanDefinitionHolders
    // allBeanNamesByType
    // singletonBeanNamesByType
    // beanDefinitionNames
    // manualSingletonNames
    // frozenBeanDefinitionNames

}
