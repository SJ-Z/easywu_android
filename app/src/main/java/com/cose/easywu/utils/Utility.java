package com.cose.easywu.utils;

import android.text.TextUtils;
import android.util.Log;

import com.cose.easywu.gson.User;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.gson.msg.LoginMsg;
import com.cose.easywu.gson.msg.PersonMsg;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

    /**
     * 解析和处理服务器返回的个人中心数据
     */
    public static PersonMsg handlePersonMsgResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            PersonMsg personMsg = new Gson().fromJson(response, PersonMsg.class);
            return personMsg;
        }
        return null;
    }

    /**
     * 解析和处理服务器返回的基本消息数据
     */
    public static BaseMsg handleBaseMsgResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            BaseMsg baseMsg = new Gson().fromJson(response, BaseMsg.class);
            return baseMsg;
        }
        return null;
    }

    /**
     * 解析和处理服务器返回的登录数据
     */
    public static LoginMsg handleLoginResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            LoginMsg loginMsg = new Gson().fromJson(response, LoginMsg.class);
            return loginMsg;
        }
        return null;
    }
}
