package com.hyf.hotrefresh;

/**
 * @author baB_hyf
 * @date 2021/12/11
 */
public class Result {

    public static String success() {
        // JSONObject rtn = new JSONObject();
        // JSONObject status = new JSONObject();
        // status.put("code", "1");
        // rtn.put("status", status);
        // return rtn.toString();
        // return "{\"status\":{\"code\":1, \"text\":\"\"}}";
        return "";
    }

    public static String error(String errorMessage) {
        // JSONObject rtn = new JSONObject();
        // JSONObject status = new JSONObject();
        // status.put("code", "0");
        // status.put("text", errorMessage);
        // rtn.put("status", status);
        // return rtn.toString();
        // return "{\"status\":{\"code\":0, \"text\":\"" + errorMessage + "\"}}";
        return errorMessage;
    }
}
