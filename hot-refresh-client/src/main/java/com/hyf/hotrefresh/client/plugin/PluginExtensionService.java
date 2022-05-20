package com.hyf.hotrefresh.client.plugin;

import com.hyf.hotrefresh.common.ExtensionService;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author baB_hyf
 * @date 2022/05/20
 */
public class PluginExtensionService implements ExtensionService {

    @Override
    public <S> List<S> getExtensionServices(Class<S> clazz) {
        ServiceLoader<S> services = ServiceLoader.load(clazz, PluginClassLoader.getInstance());
        List<S> svcs = new ArrayList<>();
        services.forEach(svcs::add);
        return svcs;
    }
}
