package com.fangtan.hourse.util;

import com.alibaba.fastjson.JSONObject;

public class DingTalk {
    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("text", getContent("叮叮叮 hello"));
        jsonObject.put("msgtype", "text");
        jsonObject.put("text", "{'content':'叮叮叮 hello'}");
        jsonObject.put("at", "{'content':'叮叮叮 hello'}");
        HttpUtil.doPost("https://oapi.dingtalk.com/robot/send?access_token=77d5209fdea46d693d3c4144de1d6bd8c154bedec28ef884f2333e241f8f3ac4", jsonObject.toJSONString(), HttpUtil.ACCEPT_JSON);
    }

    public static void push(String content){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgtype", "text");
        jsonObject.put("text", getContent(content));
        HttpUtil.doPost("https://oapi.dingtalk.com/robot/send?access_token=77d5209fdea46d693d3c4144de1d6bd8c154bedec28ef884f2333e241f8f3ac4", jsonObject.toJSONString(), HttpUtil.ACCEPT_JSON);
    }

    private static JSONObject getContent(String content){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("content", "叮叮叮  "+content);
        return jsonObject;
    }

}
