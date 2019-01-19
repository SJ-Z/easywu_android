package com.cose.easywu.utils;

import android.text.TextUtils;
import android.util.Log;

import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.gson.msg.CommentMsg;
import com.cose.easywu.gson.msg.GoodsMsg;
import com.cose.easywu.gson.msg.HXMsg;
import com.cose.easywu.gson.msg.LoginMsg;
import com.cose.easywu.gson.msg.PersonMsg;
import com.cose.easywu.gson.msg.ReleaseMsg;
import com.cose.easywu.home.bean.CommentBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Date;

public class Utility {

    /**
     * 解析和处理服务器返回的商品信息
     */
    public static GoodsMsg handleGoodsResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            return new Gson().fromJson(response, GoodsMsg.class);
        }
        return null;
    }

    /**
     * 解析和处理服务器返回的环信需要的数据
     */
    public static HXMsg handleHXMsgResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            return new Gson().fromJson(response, HXMsg.class);
        }
        return null;
    }

    /**
     * 解析和处理服务器返回的评论的数据
     */
    public static CommentMsg handleMakeCommentResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            GsonBuilder builder = new GsonBuilder();

            // Register an adapter to manage the date types as long values
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            Gson gson = builder.create();

            return gson.fromJson(response, CommentMsg.class);
        }
        return null;
    }

    /**
     * 解析和处理服务器返回的商品留言数据
     */
    public static CommentBean handleCommentResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                GsonBuilder builder = new GsonBuilder();

                // Register an adapter to manage the date types as long values
                builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                });

                Gson gson = builder.create();

                JSONObject jsonObject = new JSONObject(response);
                return gson.fromJson(jsonObject.getString("CommentBean"), CommentBean.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 解析和处理服务器返回的个人中心数据
     */
    public static PersonMsg handlePersonMsgResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            return new Gson().fromJson(response, PersonMsg.class);
        }
        return null;
    }

    /**
     * 解析和处理服务器返回的基本消息数据
     */
    public static BaseMsg handleBaseMsgResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            return new Gson().fromJson(response, BaseMsg.class);
        }
        return null;
    }

    /**
     * 解析和处理服务器返回的登录数据
     */
    public static LoginMsg handleLoginResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            return new Gson().fromJson(response, LoginMsg.class);
        }
        return null;
    }

    /**
     * 解析和处理服务器返回的商品发布数据
     */
    public static ReleaseMsg handleReleaseResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            return new Gson().fromJson(response, ReleaseMsg.class);
        }
        return null;
    }
}
