package com.hyf.hotrefresh.adapter.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 应用上下文工具
 *
 * @author baB_hyf
 * @date 2022/02/09
 */
public class ApplicationContextUtils implements ApplicationContextAware {

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

