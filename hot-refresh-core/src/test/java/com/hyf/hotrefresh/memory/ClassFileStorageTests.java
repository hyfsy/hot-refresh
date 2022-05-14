package com.hyf.hotrefresh.memory;

import com.hyf.hotrefresh.Constants;
import org.junit.Test;

import java.util.ServiceLoader;

import static org.junit.Assert.*;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public class ClassFileStorageTests {

    public static final String CLASS_NAME = "com.hyf.test.classfilestorage.Test";

    @Test
    public void testOperate() {
        MemoryClassLoader.newInstance(); // load for clear file storage home

        ServiceLoader<ClassFileStorage> classFileStorages = ServiceLoader.load(ClassFileStorage.class);
        assertTrue(classFileStorages.iterator().hasNext());

        ClassFileStorage classFileStorage = classFileStorages.iterator().next();

        assertNotNull(classFileStorage.getStorageHome());
        assertFalse(classFileStorage.getClassFile(CLASS_NAME).exists());
        classFileStorage.write(CLASS_NAME, "aaa".getBytes(Constants.MESSAGE_ENCODING));
        assertNotNull(classFileStorage.get(CLASS_NAME));
        assertTrue(classFileStorage.getClassFile(CLASS_NAME).exists());
        classFileStorage.delete(CLASS_NAME);
        assertFalse(classFileStorage.getClassFile(CLASS_NAME).exists());
        classFileStorage.clear();
    }

    public static class MockClassFileStorage extends MemoryClassFileStorage {

    }
}
