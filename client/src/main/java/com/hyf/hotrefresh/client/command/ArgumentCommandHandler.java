package com.hyf.hotrefresh.client.command;

import com.hyf.hotrefresh.client.api.command.AbstractCommandHandler;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.args.ArgumentHolder;

/**
 * @author baB_hyf
 * @date 2023/04/04
 */
public class ArgumentCommandHandler extends AbstractCommandHandler {

    @Override
    protected String getIdentity() {
        return "config";
    }

    @Override
    protected void doHandle(String[] commands) throws Exception {
        int length = commands.length;
        if (length <= 1) {
            Log.warn(getHelpString());
            return;
        }

        String opt = commands[0];

        if ("get".equals(opt) && commands.length == 2) {
            String key = commands[1];
            Log.info(ArgumentHolder.get(key));
        }
        else if ("set".equals(opt) && commands.length == 3) {
            String key = commands[1];
            String value = commands[2];
            ArgumentHolder.put(key, value);
            Log.info("success");
        }
        else {
            Log.warn(getHelpString());
        }
    }

    private String getHelpString() {
        return "Unknown command\n\n\tconfig get [key]\n\tconfig set [key] [value]\n";
    }
}
