package com.hyf.hotrefresh.plugin.arthas;

import com.hyf.hotrefresh.common.util.ReflectionUtils;
import com.hyf.hotrefresh.core.util.Util;

/**
 * @author baB_hyf
 * @date 2022/06/26
 */
public abstract class ArthasUtils {

    public static boolean existArthasConfiguration() {
        try {
            String className = "com.alibaba.arthas.spring.ArthasConfiguration";
            ClassLoader ccl = Util.getOriginContextClassLoader();
            ReflectionUtils.forName(className, ccl);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

}
