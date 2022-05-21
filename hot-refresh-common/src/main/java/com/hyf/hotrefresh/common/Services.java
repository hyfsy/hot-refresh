package com.hyf.hotrefresh.common;

import java.util.*;

/**
 * @author baB_hyf
 * @date 2022/05/20
 */
public class Services {

    private static final List<ExtensionService> extendServices = new ArrayList<>();

    static {
        ServiceLoader<ExtensionService> services = ServiceLoader.load(ExtensionService.class);
        services.forEach(extendServices::add);
    }

    public static <S> List<S> gets(Class<S> clazz) {
        return gets(clazz, Thread.currentThread().getContextClassLoader());
    }

    public static <S> List<S> gets(Class<S> clazz, ClassLoader classLoader) {

        List<S> svcs = new ArrayList<>();

        ServiceLoader<S> services = ServiceLoader.load(clazz, classLoader);
        services.forEach(svcs::add);

        for (ExtensionService extendService : extendServices) {
            try {
                List<S> extendServices = extendService.getExtensionServices(clazz);
                svcs.addAll(extendServices);
            } catch (Throwable t) {
                Log.error("Get extend services failed", t);
            }
        }

        filterSameClass(svcs);
        return svcs;
    }

    @SuppressWarnings("unchecked")
    private static <S> void filterSameClass(List<S> svcs) {
        Set<Class<S>> classSet = new HashSet<>();
        svcs.removeIf(svc -> !classSet.add((Class<S>) svc.getClass()));
    }
}
