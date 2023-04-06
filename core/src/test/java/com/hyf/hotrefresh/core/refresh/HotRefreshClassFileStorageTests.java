package com.hyf.hotrefresh.core.refresh;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.common.Services;
import com.hyf.hotrefresh.core.util.Util;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class HotRefreshClassFileStorageTests {

    private static final String CLASS_NAME = "com.hyf.test.classfilestorage.Test";

    @Test
    public void testOperate() {
        List<ClassFileStorage> classFileStorages = Services.gets(ClassFileStorage.class);
        assertTrue(classFileStorages.iterator().hasNext());

        ClassFileStorage classFileStorage = classFileStorages.iterator().next();

        assertNotNull(classFileStorage.getStorageHome());
        assertFalse(classFileStorage.getClassFile(CLASS_NAME).exists());
        classFileStorage.write(CLASS_NAME, "aaa".getBytes(Constants.MESSAGE_ENCODING));
        assertNotNull(classFileStorage.get(CLASS_NAME));
        assertTrue(classFileStorage.getClassFile(CLASS_NAME).exists());
        classFileStorage.delete(CLASS_NAME);
        assertFalse(classFileStorage.getClassFile(CLASS_NAME).exists());
        classFileStorage.write(CLASS_NAME, "aaa".getBytes(Constants.MESSAGE_ENCODING));
        classFileStorage.clear();
        assertFalse(classFileStorage.getClassFile(CLASS_NAME).exists());
    }

    public static class MockClassFileStorage extends HotRefreshClassFileStorage {

    }
}
