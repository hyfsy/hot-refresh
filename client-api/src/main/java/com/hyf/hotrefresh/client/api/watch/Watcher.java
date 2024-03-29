package com.hyf.hotrefresh.client.api.watch;

import com.hyf.hotrefresh.common.ChangeType;

import java.io.File;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public interface Watcher {

    default boolean interest(Object context) {
        return true;
    }

    default void onChange(File file, ChangeType type) {
    }

    default void onDelete(File file) {
    }

    default void onCreate(File file) {
        onChange(file, ChangeType.CREATE);
    }

    default void onModify(File file) {
        onChange(file, ChangeType.MODIFY);
    }

    void startWatch();

    void stopWatch();

}
