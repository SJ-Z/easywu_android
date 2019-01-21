package com.cose.easywu.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.cose.easywu.app.WelcomeActivity;
import com.cose.easywu.db.Notification;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.NotificationHelper;
import com.hyphenate.easeui.model.GoodsMessageHelper;

import org.litepal.LitePal;

import cn.jpush.android.api.JPushInterface;

public class MyJPushReceiver extends BroadcastReceiver {
    private static final String TAG = "MyJPushReceiver";
    private LocalBroadcastManager localBroadcastManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (localBroadcastManager == null) {
            localBroadcastManager = LocalBroadcastManager.getInstance(context);
        }
        Bundle bundle = intent.getExtras();
        String content = bundle.getString(JPushInterface.EXTRA_ALERT);
        String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
        JSONObject jsonObject = JSONObject.parseObject(extra);

        if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) { // 收到了通知
            Log.d(TAG, "[MyReceiver] 用户收到了通知");
            // 发送广播
            Intent i = new Intent(Constant.RECEIVE_NEW_MESSAGE);
            localBroadcastManager.sendBroadcast(i);

            if (jsonObject.getBoolean(GoodsMessageHelper.CHATTYPE)) { // 为true说明是商品评论回复的通知
                Notification notification;
                if (jsonObject.getInteger(GoodsMessageHelper.MESSAGE_TYPE) == NotificationHelper.TYPE_GOODS_COMMENT) {
                    notification = new Notification(NotificationHelper.GOODS, content,
                            jsonObject.getString(GoodsMessageHelper.GOODS_ID),
                            jsonObject.getLong(GoodsMessageHelper.MESSAGE_TIME),
                            NotificationHelper.TYPE_GOODS_COMMENT);
                } else {
                    notification = new Notification(NotificationHelper.GOODS, content,
                            jsonObject.getString(GoodsMessageHelper.GOODS_ID),
                            jsonObject.getLong(GoodsMessageHelper.MESSAGE_TIME),
                            NotificationHelper.TYPE_GOODS_REPLY);
                }
                notification.save();
            }
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) { // 通知被点击
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            // 修改数据库中该通知的状态为已读
            Notification notification = LitePal.where("time=?", jsonObject.getLong(
                    GoodsMessageHelper.MESSAGE_TIME) + "").findFirst(Notification.class);
            notification.setState(NotificationHelper.STATE_READ);
            notification.save();

            //打开自定义的Activity
            Intent i = new Intent(context, WelcomeActivity.class);
            i.putExtra(GoodsMessageHelper.CHATTYPE, true); // 让GoodsInfoActivity识别的标志位
            i.putExtra(GoodsMessageHelper.GOODS_ID, jsonObject.getString("g_id"));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
