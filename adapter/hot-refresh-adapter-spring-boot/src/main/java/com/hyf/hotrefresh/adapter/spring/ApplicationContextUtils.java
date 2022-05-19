package com.hyf.hotrefresh.adapter.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 应用上下文工具
 *
 * @author baB_hyf
 * @date 2022/02/09
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware, BeanFactoryAware {

    private static volatile ConfigurableListableBeanFactory beanFactory;

    private static volatile ApplicationContext ctx;

    public static ApplicationContext getApplicationContext() {
        if (ctx == null) {
            throw new IllegalStateException("ApplicationContext has not been initialized");
        }
        return ctx;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtils.ctx = applicationContext;
    }

    public static ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new RuntimeException("Unknown bean factory type: " + beanFactory.getClass().getName());
        }
        ApplicationContextUtils.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
    }

    public static <T> T getBean(Class<T> clazz) {
        return getBean(getApplicationContext(), clazz);
    }

    public static <T> T getBean(ApplicationContext ctx, Class<T> clazz) {
        return ctx.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getBean(getApplicationContext(), name, clazz);
    }

    public static <T> T getBean(ApplicationContext ctx, String name, Class<T> clazz) {
        return ctx.getBean(name, clazz);
    }

    public static <T> T getBeanIfExist(Class<T> clazz) {
        ApplicationContext ctx = getApplicationContext();

        try {
            return ctx.getBean(clazz);
        } catch (NoSuchBeanDefinitionException ignored) {
            return null;
        }
    }

    public static <T> T getBeanIfExist(String name, Class<T> clazz) {
        return getBeanIfExist(getApplicationContext(), name, clazz);
    }

    public static <T> T getBeanIfExist(ApplicationContext ctx, String name, Class<T> clazz) {
        if (ctx.containsBean(name)) {
            return ctx.getBean(name, clazz);
        }

        return null;
    }
}

