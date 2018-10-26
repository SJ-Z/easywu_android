package com.cose.easywu.utils;

import android.text.TextUtils;

import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.gson.msg.LoginMsg;

import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

    /**
     * 解析和处理服务器返回的基本消息数据
     */
    public static BaseMsg handleBaseMsgResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject msgObject = new JSONObject(response);
                BaseMsg baseMsg = new BaseMsg();
                baseMsg.setCode(msgObject.getString("code"));
                baseMsg.setMsg(msgObject.getString("msg"));
                return baseMsg;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 解析和处理服务器返回的登录数据
     */
    public static LoginMsg handleLoginResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject msgObject = new JSONObject(response);
                LoginMsg loginMsg = new LoginMsg();
                loginMsg.setCode(msgObject.getString("code"));
                loginMsg.setMsg(msgObject.getString("msg"));
                loginMsg.setU_id(msgObject.getString("u_id"));
                return loginMsg;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
