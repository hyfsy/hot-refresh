package com.hyf.hotrefresh.plugin.execute.executable;

import com.hyf.hotrefresh.common.args.ArgumentHolder;
import com.hyf.hotrefresh.common.Constants;
import com.hyf.hotrefresh.plugin.execute.Executable;

/**
 * @author baB_hyf
 * @date 2022/05/28
 */
public class CloseDebugExecutable implements Executable<Void> {

    @Override
    public Void execute() {
        ArgumentHolder.put(Constants.ARG_DEBUG, false);
        return null;
    }
}