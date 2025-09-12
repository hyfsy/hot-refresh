package com.hyf.hotrefresh.core.refresh;

import com.hyf.hotrefresh.common.ChangeType;
import com.hyf.hotrefresh.core.TestJavaFileUtils;
import com.hyf.hotrefresh.core.exception.RefreshException;
import com.hyf.hotrefresh.core.util.Util;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class HotRefresherTests {

    @Test(expected = RuntimeException.class)
    public void testNotRefresh() {
        Util.getThrowawayHotRefreshClassLoader().getClass(UUID.randomUUID().toString() + TestJavaFileUtils.getClassName());
    }

    @Test
    public void testJavaFileRefreshed() throws RefreshException {
        HotRefresher.refresh(TestJavaFileUtils.getFileName(), TestJavaFileUtils.getContent().getBytes(StandardCharsets.UTF_8), ChangeType.MODIFY.name());
        assertNotNull(Util.getThrowawayHotRefreshClassLoader().getClass(TestJavaFileUtils.getClassName()));
        Util.getThrowawayHotRefreshClassLoader().clear();
    }

    @Test
    public void testClassFileRefreshed() throws RefreshException {
        HotRefresher.refresh(TestJavaFileUtils.getClassFileName(), TestJavaFileUtils.getClassBytes(), ChangeType.MODIFY.name());
        assertNotNull(Util.getThrowawayHotRefreshClassLoader().getClass(TestJavaFileUtils.getClassName()));
        Util.getThrowawayHotRefreshClassLoader().clear();
    }
}
