package com.hyf.hotrefresh.plugin.execute.executable;

import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.plugin.execute.Executable;

/**
 * @author baB_hyf
 * @date 2022/05/28
 */
public class DebugExecutable implements Executable<Void> {

    @Override
    public Void execute() {
        Constants.DEBUG = true;
        return null;
    }
}
