package com.hyf.hotrefresh.refresh;

import com.hyf.hotrefresh.ChangeType;
import com.hyf.hotrefresh.TestJavaFileUtils;
import com.hyf.hotrefresh.exception.RefreshException;
import com.hyf.hotrefresh.memory.MemoryClassLoader;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class HotRefresherTests {

    @Test(expected = RuntimeException.class)
    public void testNotRefresh() {
        MemoryClassLoader.newInstance().getClass(TestJavaFileUtils.getClassName());
    }

    @Test
    public void testHasRefreshed() throws RefreshException {
        HotRefresher.refresh(TestJavaFileUtils.getFileName(), TestJavaFileUtils.getContent(), ChangeType.MODIFY.name());
        assertNotNull(MemoryClassLoader.newInstance().getClass(TestJavaFileUtils.getClassName()));
    }
}
