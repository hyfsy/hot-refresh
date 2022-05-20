package com.hyf.hotrefresh.common;

import java.util.List;

/**
 * @author baB_hyf
 * @date 2022/05/20
 */
public interface ExtensionService {

    <S> List<S> getExtensionServices(Class<S> clazz);
}
