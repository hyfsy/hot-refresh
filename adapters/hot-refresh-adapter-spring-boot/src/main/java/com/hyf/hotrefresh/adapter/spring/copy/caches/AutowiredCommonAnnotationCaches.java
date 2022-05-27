package com.hyf.hotrefresh.adapter.spring.copy.caches;

import com.hyf.hotrefresh.common.util.FastReflectionUtils;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;

import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/05/22
 */
public class AutowiredCommonAnnotationCaches {

    public static void reset(DefaultListableBeanFactory bf, String name) {

        CommonAnnotationBeanPostProcessor common = bf.getBean(CommonAnnotationBeanPostProcessor.class);
        FastReflectionUtils.fastGetFieldNoException(common, CommonAnnotationBeanPostProcessor.class, "injectionMetadataCache")
                .ifPresent(map -> ((Map) map).remove(name));

        AutowiredAnnotationBeanPostProcessor autowired = bf.getBean(AutowiredAnnotationBeanPostProcessor.class);
        FastReflectionUtils.fastGetFieldNoException(autowired, AutowiredAnnotationBeanPostProcessor.class, "injectionMetadataCache")
                .ifPresent(map -> ((Map) map).remove(name));
    }
}
