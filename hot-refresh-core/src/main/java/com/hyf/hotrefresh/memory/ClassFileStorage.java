package com.hyf.hotrefresh.memory;

import com.hyf.hotrefresh.Constants;

import java.io.File;

/**
 * @author baB_hyf
 * @date 2022/05/14
 */
public interface ClassFileStorage {

    String OUTPUT_HOME = Constants.REFRESH_HOME + File.separator + "output";

    String getStorageHome();

    void write(String className, byte[] bytes);

    void delete(String className);

    void clear();

    byte[] get(String className);

    File getClassFile(String className);
}
