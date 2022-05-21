package com.hyf.hotrefresh.core.memory;

import com.hyf.hotrefresh.common.Services;
import org.junit.Test;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class AnnotationProcessorCompositeClassLoaderTests {

    @Test
    public void testLoadAnnotationProcessor() {
        List<OtherProcessorToAvoidJavacError> processors = Services.gets(OtherProcessorToAvoidJavacError.class, AnnotationProcessorCompositeClassLoader.getInstance());
        assertTrue(processors.iterator().hasNext());
        assertThat(processors.iterator().next(), instanceOf(MockProcessor.class));
    }

    public interface OtherProcessorToAvoidJavacError {
        boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment environment);
    }

    public static class MockProcessor implements OtherProcessorToAvoidJavacError {

        @Override
        public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
            return false;
        }
    }
}
