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
package com.hyf.hotrefresh.adapter.spring.copy.caches;

import com.hyf.hotrefresh.common.Log;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.util.Map;

import static com.hyf.hotrefresh.common.util.FastReflectionUtils.*;

/**
 * Reset various Spring static caches. It is safe to run multiple times,
 * basically every time any configuration is changed.
 *
 * @author Jiri Bubnik
 * @author baB_hyf
 * @date 2022/05/27
 */
public class ResetSpringStaticCaches {

    /**
     * Spring bean by type cache.
     * <p>
     * Cache names change between versions, call via reflection and ignore errors.
     */
    public static void resetBeanNamesByType(DefaultListableBeanFactory defaultListableBeanFactory) {
        try {
            ((Map) fastGetField(defaultListableBeanFactory, DefaultListableBeanFactory.class, "singletonBeanNamesByType")).clear();
        } catch (Exception e) {
            if (Log.isDebugMode()) {
                Log.error("Unable to clear DefaultListableBeanFactory.singletonBeanNamesByType cache (is Ok for pre 3.1.2 Spring version)", e);
            }
        }

        try {
            ((Map) fastGetField(defaultListableBeanFactory, DefaultListableBeanFactory.class, "allBeanNamesByType")).clear();
        } catch (Exception e) {
            if (Log.isDebugMode()) {
                Log.debug("Unable to clear allBeanNamesByType cache (is Ok for pre 3.2 Spring version)");
            }
        }

        try {
            ((Map) fastGetField(defaultListableBeanFactory, DefaultListableBeanFactory.class, "nonSingletonBeanNamesByType")).clear();
        } catch (Exception e) {
            if (Log.isDebugMode()) {
                Log.debug("Unable to clear nonSingletonBeanNamesByType cache (is Ok for pre 3.2 Spring version)");
            }
        }
    }

    /**
     * Reset all caches.
     */
    public static void reset() {
        resetTypeVariableCache();
        resetAnnotationUtilsCache();
        resetReflectionUtilsCache();
        resetResolvableTypeCache();
        resetPropertyCache();
        resetIntrospectionCache();
    }

    private static void resetResolvableTypeCache() {
        fastInvokeMethodNoException(ResolvableType.class, "clearCache");
    }

    private static void resetTypeVariableCache() {
        try {
            ((Map) fastGetField(GenericTypeResolver.class, "typeVariableCache")).clear();
            if (Log.isDebugMode()) {
                Log.debug("Cache cleared: GenericTypeResolver.typeVariableCache");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to clear GenericTypeResolver.typeVariableCache", e);
        }
    }

    private static void resetReflectionUtilsCache() {

        fastInvokeMethodNoException(ReflectionUtils.class, "clearCache");

        fastGetFieldNoException(ReflectionUtils.class, "declaredMethodsCache")
                .ifPresent(map -> {
                    ((Map) map).clear();
                    if (Log.isDebugMode()) {
                        Log.debug("Cache cleared: ReflectionUtils.declaredMethodsCache");
                    }
                });
    }

    private static void resetAnnotationUtilsCache() {

        fastInvokeMethodNoException(AnnotationUtils.class, "clearCache");

        fastGetFieldNoException(AnnotationUtils.class, "annotatedInterfaceCache")
                .ifPresent(map -> {
                    ((Map) map).clear();
                    if (Log.isDebugMode()) {
                        Log.debug("Cache cleared: AnnotationUtils.annotatedInterfaceCache");
                    }
                });

        fastGetFieldNoException(AnnotationUtils.class, "findAnnotationCache")
                .ifPresent(map -> {
                    ((Map) map).clear();
                    if (Log.isDebugMode()) {
                        Log.debug("Cache cleared: AnnotationUtils.findAnnotationCache");
                    }
                });

    }

    private static void resetPropertyCache() {
        try {
            Class<?> c = ResetSpringStaticCaches.class.getClassLoader().loadClass("org.springframework.core.convert.Property");
            ((Map) fastGetField(c, "annotationCache")).clear();
            if (Log.isDebugMode()) {
                Log.debug("Cache cleared: Property.annotationCache");
            }
        } catch (Exception e) {
            if (Log.isDebugMode()) {
                Log.error("Unable to clear Property.annotationCache (ok before Spring 3.2.x)", e);
            }
        }
    }

    private static void resetIntrospectionCache() {
        CachedIntrospectionResults.clearClassLoader(ResetSpringStaticCaches.class.getClassLoader());
    }
}
