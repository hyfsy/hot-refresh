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
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.Map;

import static com.hyf.hotrefresh.common.util.FastReflectionUtils.fastGetField;
import static com.hyf.hotrefresh.common.util.FastReflectionUtils.fastInvokeMethod;

/**
 * Spring Bean post processors contain various caches for performance reasons. Clear the caches on reload.
 *
 * @author Jiri Bubnik
 * @author baB_hyf
 * @date 2022/05/27
 */
public class ResetBeanPostProcessorCaches {

    private static Class<?> getReflectionUtilsClassOrNull() {
        try {
            //This is probably a bad idea as Class.forName has lots of issues but this was easiest for now.
            return Class.forName("org.springframework.util.ReflectionUtils");
        } catch (ClassNotFoundException e) {
            if (Log.isDebugMode()) {
                Log.debug("Spring 4.1.x or below - ReflectionUtils class not found");
            }
            return null;
        }
    }

    /**
     * Reset all post processors associated with a bean factory.
     *
     * @param beanFactory beanFactory to use
     */
    public static void reset(DefaultListableBeanFactory beanFactory) {
        Class<?> c = getReflectionUtilsClassOrNull();
        if (c != null) {
            try {
                fastInvokeMethod(c, "clearCache");
            } catch (Exception version42Failed) {
                try {
                    // spring 4.0.x, 4.1.x without clearCache method, clear manually
                    ((Map) fastGetField(c, "declaredMethodsCache")).clear();
                    ((Map) fastGetField(c, "declaredFieldsCache")).clear();
                } catch (Exception version40Failed) {
                    if (Log.isDebugMode()) {
                        Log.error("Failed to clear internal method/field cache, it's normal with spring 4.1x or lower", version40Failed);
                    }
                }
            }
            if (Log.isDebugMode()) {
                Log.debug("Cleared Spring 4.2+ internal method/field cache.");
            }
        }
        for (BeanPostProcessor bpp : beanFactory.getBeanPostProcessors()) {
            if (bpp instanceof AutowiredAnnotationBeanPostProcessor) {
                resetAutowiredAnnotationBeanPostProcessorCache((AutowiredAnnotationBeanPostProcessor) bpp);
            }
            else if (bpp instanceof InitDestroyAnnotationBeanPostProcessor) {
                resetInitDestroyAnnotationBeanPostProcessorCache((InitDestroyAnnotationBeanPostProcessor) bpp);
            }
        }
    }

    public static void resetInitDestroyAnnotationBeanPostProcessorCache(InitDestroyAnnotationBeanPostProcessor bpp) {
        try {
            ((Map) fastGetField(bpp, InitDestroyAnnotationBeanPostProcessor.class, "lifecycleMetadataCache")).clear();
            if (Log.isDebugMode()) {
                Log.debug("Cache cleared: InitDestroyAnnotationBeanPostProcessor.lifecycleMetadataCache");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to clear InitDestroyAnnotationBeanPostProcessor.lifecycleMetadataCache", e);
        }
    }

    // @Autowired cache
    public static void resetAutowiredAnnotationBeanPostProcessorCache(AutowiredAnnotationBeanPostProcessor bpp) {
        try {
            ((Map) fastGetField(bpp, AutowiredAnnotationBeanPostProcessor.class, "candidateConstructorsCache")).clear();
            if (Log.isDebugMode()) {
                Log.debug("Cache cleared: AutowiredAnnotationBeanPostProcessor.candidateConstructorsCache");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to clear AutowiredAnnotationBeanPostProcessor.candidateConstructorsCache", e);
        }

        try {
            ((Map) fastGetField(bpp, AutowiredAnnotationBeanPostProcessor.class, "injectionMetadataCache")).clear();
            if (Log.isDebugMode()) {
                Log.debug("Cache cleared: AutowiredAnnotationBeanPostProcessor.injectionMetadataCache");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to clear AutowiredAnnotationBeanPostProcessor.injectionMetadataCache", e);
        }

    }
}
