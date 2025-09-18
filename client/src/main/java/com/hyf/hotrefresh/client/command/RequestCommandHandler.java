package com.hyf.hotrefresh.client.command;

import com.hyf.hotrefresh.client.api.command.AbstractCommandHandler;
import com.hyf.hotrefresh.client.core.interceptor.RequestCommand;
import com.hyf.hotrefresh.common.Log;

import java.util.Map;

/**
 * @author baB_hyf
 * @date 2023/04/07
 */
public class RequestCommandHandler extends AbstractCommandHandler {

    @Override
    protected String getIdentity() {
        return "request";
    }

    @Override
    protected void doHandle(String[] commands) throws Exception {
        if (commands.length < 2) {
            Log.warn(getHelpString());
            return;
        }

        String opt = commands[0];

        switch (opt) {
            case "get":
                handleGet(commands);
                break;
            case "set":
                handleSet(commands);
                break;
            case "remove":
                handleRemove(commands);
                break;
            case "list":
                handleList(commands);
                break;
            default:
                Log.warn(getHelpString());
        }
    }

    private void handleGet(String[] commands) {
        if (commands.length != 3) {
            Log.warn(getHelpString());
            return;
        }

        String type = commands[1];
        String key = commands[2];

        switch (type) {
            case "url":
                Log.info(RequestCommand.getUrl());
                break;
            case "param":
                Log.info(String.valueOf(RequestCommand.getParams().get(key)));
                break;
            case "header":
                Log.info(String.valueOf(RequestCommand.getHeaders().get(key)));
                break;
            case "cookie":
                Log.info(String.valueOf(RequestCommand.getCookies().get(key)));
                break;
            default:
                Log.warn(getHelpString());
        }
    }

    private void handleSet(String[] commands) {
        if (commands.length < 3) {
            Log.warn(getHelpString());
            return;
        }

        String type = commands[1];

        switch (type) {
            case "url":
                if (commands.length != 3) {
                    Log.warn(getHelpString());
                    return;
                }
                RequestCommand.setUrl(commands[2]);
                Log.info("success");
                break;
            case "param":
                if (commands.length != 4) {
                    Log.warn(getHelpString());
                    return;
                }
                RequestCommand.getParams().put(commands[2], commands[3]);
                Log.info("success");
                break;
            case "header":
                if (commands.length != 4) {
                    Log.warn(getHelpString());
                    return;
                }
                RequestCommand.getHeaders().put(commands[2], commands[3]);
                Log.info("success");
                break;
            case "cookie":
                if (commands.length != 4) {
                    Log.warn(getHelpString());
                    return;
                }
                RequestCommand.getCookies().put(commands[2], commands[3]);
                Log.info("success");
                break;
            default:
                Log.warn(getHelpString());
        }
    }

    private void handleRemove(String[] commands) {
        if (commands.length < 2) {
            Log.warn(getHelpString());
            return;
        }

        String type = commands[1];

        switch (type) {
            case "url":
                if (commands.length != 2) {
                    Log.warn(getHelpString());
                    return;
                }
                RequestCommand.setUrl(null);
                Log.info("success");
                break;
            case "param":
                if (commands.length != 3) {
                    Log.warn(getHelpString());
                    return;
                }
                RequestCommand.getParams().remove(commands[2]);
                Log.info("success");
                break;
            case "header":
                if (commands.length != 3) {
                    Log.warn(getHelpString());
                    return;
                }
                RequestCommand.getHeaders().remove(commands[2]);
                Log.info("success");
                break;
            case "cookie":
                if (commands.length != 3) {
                    Log.warn(getHelpString());
                    return;
                }
                RequestCommand.getCookies().remove(commands[2]);
                Log.info("success");
                break;
            default:
                Log.warn(getHelpString());
        }
    }

    private void handleList(String[] commands) {
        if (commands.length != 2) {
            Log.warn(getHelpString());
            return;
        }

        String type = commands[1];

        switch (type) {
            case "url":
                Log.info(RequestCommand.getUrl());
                break;
            case "param":
                printMap("params", RequestCommand.getParams());
                break;
            case "header":
                printMap("headers", RequestCommand.getHeaders());
                break;
            case "cookie":
                printMap("cookies", RequestCommand.getCookies());
                break;
            default:
                Log.warn(getHelpString());
        }
    }

    private void printMap(String title, Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(title).append(":\n");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        Log.info(sb.toString());
    }

    private String getHelpString() {
        return "Unknown command\n\n" +
                "\trequest get url\n" +
                "\trequest get param [key]\n" +
                "\trequest set url [value]\n" +
                "\trequest set param [key] [value]\n" +
                "\trequest remove url\n" +
                "\trequest remove param [key]\n" +
                "\trequest remove header [key]\n" +
                "\trequest remove cookie [key]\n" +
                "\trequest list url\n" +
                "\trequest list param\n" +
                "\trequest list header\n" +
                "\trequest list cookie\n";
    }
}
