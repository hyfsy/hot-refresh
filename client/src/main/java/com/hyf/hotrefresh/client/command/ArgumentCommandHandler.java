package com.hyf.hotrefresh.client.command;

import com.hyf.hotrefresh.client.api.command.AbstractCommandHandler;
import com.hyf.hotrefresh.common.Log;
import com.hyf.hotrefresh.common.args.ArgumentHolder;

import java.util.Map;

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
        if (commands.length < 1) {
            Log.warn(getHelpString());
            return;
        }

        String opt = commands[0];

        if ("get".equals(opt) && commands.length == 2) {
            String key = commands[1];
            Log.info(String.valueOf((Object) ArgumentHolder.get(key)));
        }
        else if ("set".equals(opt) && commands.length == 3) {
            String key = commands[1];
            String value = commands[2];
            ArgumentHolder.put(key, value);
            Log.info("success");
        }
        else if ("remove".equals(opt) && commands.length == 2) {
            String key = commands[1];
            ArgumentHolder.remove(key);
            Log.info("success");
        }
        else if ("list".equals(opt) && commands.length == 1) {
            Map<String, Object> map = ArgumentHolder.getMap();
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
            }
            Log.info(sb.toString());
        }
        else {
            Log.warn(getHelpString());
        }
    }

    private String getHelpString() {
        return "Unknown command\n\n" +
                "\tconfig get [key]\n" +
                "\tconfig set [key] [value]\n" +
                "\tconfig remove [key]\n" +
                "\tconfig list\n";
    }
}
