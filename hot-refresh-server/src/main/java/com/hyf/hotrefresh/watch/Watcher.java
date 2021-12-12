package com.hyf.hotrefresh.watch;

import java.io.File;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public interface Watcher {

    default boolean interest(Object context) {
        return true;
    }

    default void onChange(File file, Type type) {
    }

    default void onDelete(File file) {
    }

    default void onCreate(File file) {
        onChange(file, Type.CREATE);
    }

    default void onModify(File file) {
        onChange(file, Type.MODIFY);
    }

    enum Type {
        CREATE, MODIFY, DELETE
    }
}
