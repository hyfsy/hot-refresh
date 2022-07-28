package com.hyf.hotrefresh.plugin.execute.executable;

import com.hyf.hotrefresh.common.Version;
import com.hyf.hotrefresh.plugin.execute.Executable;

/**
 * @author baB_hyf
 * @date 2022/07/24
 */
public class GetVersionExecutable implements Executable<String> {
    @Override
    public String execute() throws Exception {
        return Version.getVersion();
    }
}
