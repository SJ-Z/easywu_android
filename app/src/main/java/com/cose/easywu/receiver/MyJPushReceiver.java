package com.cose.easywu.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.cose.easywu.app.WelcomeActivity;
import com.hyphenate.easeui.model.GoodsMessageHelper;

import cn.jpush.android.api.JPushInterface;

public class MyJPushReceiver extends BroadcastReceiver {
    private static final String TAG = "MyJPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            Bundle bundle = intent.getExtras();
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            JSONObject jsonObject = JSONObject.parseObject(extra);
            //打开自定义的Activity
            Intent i = new Intent(context, WelcomeActivity.class);
            i.putExtra(GoodsMessageHelper.CHATTYPE, true); // 让GoodsInfoActivity识别的标志位
            i.putExtra(GoodsMessageHelper.GOODS_ID, jsonObject.getString("g_id"));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
