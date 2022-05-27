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
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import static com.hyf.hotrefresh.common.util.FastReflectionUtils.fastGetField;
import static com.hyf.hotrefresh.common.util.FastReflectionUtils.fastInvokeMethod;
import static com.hyf.hotrefresh.common.util.ReflectionUtils.getMethod;
import static com.hyf.hotrefresh.common.util.ReflectionUtils.invokeMethod;

/**
 * Support for Spring MVC mapping caches.
 *
 * @author baB_hyf
 * @date 2022/05/27
 */
public class ResetRequestMappingCaches {

    private static Class<?> getHandlerMethodMappingClassOrNull() {
        try {
            //This is probably a bad idea as Class.forName has lots of issues but this was easiest for now.
            return Class.forName("org.springframework.web.servlet.handler.AbstractHandlerMethodMapping");
        } catch (ClassNotFoundException e) {
            if (Log.isDebugMode()) {
                Log.debug("HandlerMethodMapping class not found");
            }
            return null;
        }
    }

    public static void reset(DefaultListableBeanFactory beanFactory) {

        Class<?> c = getHandlerMethodMappingClassOrNull();
        if (c == null) {
            return;
        }

        Map<String, ?> mappings =
                BeanFactoryUtils.beansOfTypeIncludingAncestors(beanFactory, c, true, false);
        if (mappings.isEmpty()) {
            if (Log.isDebugMode()) {
                Log.debug("Spring: no HandlerMappings found");
            }
        }
        try {
            for (Entry<String, ?> e : mappings.entrySet()) {
                Object am = e.getValue();
                if (Log.isDebugMode()) {
                    Log.debug("Spring: clearing HandlerMapping for " + am.getClass());
                }
                try {
                    ((Map) fastGetField(c, "handlerMethods")).clear();
                    ((Map) fastGetField(c, "urlMap")).clear();
                    try {
                        ((Map) fastGetField(c, "nameMap")).clear();
                    } catch (Exception nsfe) {
                        if (Log.isDebugMode()) {
                            Log.error("Probably using Spring 4.0 or below", nsfe);
                        }
                    }
                } catch (Exception nsfe) {
                    if (Log.isDebugMode()) {
                        Log.error("Probably using Spring 4.2+", nsfe);
                    }
                    Class<?>[] parameterTypes = new Class[1];
                    parameterTypes[0] = Object.class;

                    Object[] keys = ((Map) fastInvokeMethod(am, c, "getHandlerMethods")).keySet().toArray();
                    Method u = getMethod(c, "unregisterMapping", parameterTypes);
                    for (Object key : keys) {
                        if (Log.isDebugMode()) {
                            Log.debug("Unregistering handler method " + key);
                        }
                        invokeMethod(u, am, key);
                    }
                }
                if (am instanceof InitializingBean) {
                    ((InitializingBean) am).afterPropertiesSet();
                }
            }
        } catch (Exception e) {
            Log.error("Failed to clear HandlerMappings", e);
        }
    }
}
