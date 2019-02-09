package com.cose.easywu.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.cose.easywu.app.WelcomeActivity;
import com.cose.easywu.db.BuyGoods;
import com.cose.easywu.db.Notification;
import com.cose.easywu.db.ReleaseGoods;
import com.cose.easywu.db.SellGoods;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.NotificationHelper;
import com.hyphenate.easeui.model.GoodsMessageHelper;

import org.litepal.LitePal;

import java.util.Date;

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

            if (null != jsonObject.getBoolean(GoodsMessageHelper.CHATTYPE) &&
                    jsonObject.getBoolean(GoodsMessageHelper.CHATTYPE)) { // 为true说明是商品评论回复的通知
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
            } else if (null != jsonObject.getBoolean(GoodsMessageHelper.NewGoodsOrderType) &&
                    jsonObject.getBoolean(GoodsMessageHelper.NewGoodsOrderType)) { // 为true说明是商品下单的通知
                String g_id = jsonObject.getString(GoodsMessageHelper.GOODS_ID);
                long orderTime = jsonObject.getLong(GoodsMessageHelper.MESSAGE_TIME);
                String g_buyer_id = jsonObject.getString(GoodsMessageHelper.GOODS_BUYER_ID);
                // 更新本地数据库
                ReleaseGoods releaseGoods = LitePal.where("g_id=?", g_id).findFirst(ReleaseGoods.class);
                SellGoods sellGoods = new SellGoods(releaseGoods.getG_id(), releaseGoods.getG_name(),
                        releaseGoods.getG_desc(), releaseGoods.getG_price(), releaseGoods.getG_originalPrice(),
                        releaseGoods.getG_pic1(), releaseGoods.getG_pic2(), releaseGoods.getG_pic3(),
                        5, releaseGoods.getG_like(), new Date(orderTime), releaseGoods.getG_t_id(), g_buyer_id);
                sellGoods.save();
                releaseGoods.delete();

                Notification notification = new Notification(NotificationHelper.GOODS, content, g_id,
                        orderTime, NotificationHelper.TYPE_NEW_ORDER_GOODS);
                notification.save();
            } else if (null != jsonObject.getBoolean(GoodsMessageHelper.ConfirmGoodsOrderType) &&
                    jsonObject.getBoolean(GoodsMessageHelper.ConfirmGoodsOrderType)) { // 为true说明是确认商品订单的通知
                String g_id = jsonObject.getString(GoodsMessageHelper.GOODS_ID);
                Notification notification = new Notification(NotificationHelper.GOODS, content,
                        g_id, jsonObject.getLong(GoodsMessageHelper.MESSAGE_TIME),
                        NotificationHelper.TYPE_CONFIRM_ORDER_GOODS);
                notification.save();
                // 修改本地数据库“我买到的”商品状态
                BuyGoods buyGoods = LitePal.where("g_id=?", g_id).findFirst(BuyGoods.class);
                buyGoods.setG_state(1);
                buyGoods.save();
            } else if (null != jsonObject.getBoolean(GoodsMessageHelper.RefuseGoodsOrderType) &&
                    jsonObject.getBoolean(GoodsMessageHelper.RefuseGoodsOrderType)) { // 为true说明是拒绝商品订单的通知
                String g_id = jsonObject.getString(GoodsMessageHelper.GOODS_ID);
                Notification notification = new Notification(NotificationHelper.GOODS, content,
                        g_id, jsonObject.getLong(GoodsMessageHelper.MESSAGE_TIME),
                        NotificationHelper.TYPE_REFUSE_ORDER_GOODS);
                notification.save();
                // 删除本地数据库“我买到的”该商品
                BuyGoods buyGoods = LitePal.where("g_id=?", g_id).findFirst(BuyGoods.class);
                buyGoods.delete();
            }
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) { // 通知被点击
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            // 修改数据库中该通知的状态为已读
            Notification notification = LitePal.where("time=?", jsonObject.getLong(
                    GoodsMessageHelper.MESSAGE_TIME) + "").findFirst(Notification.class);
            notification.setState(NotificationHelper.STATE_READ);
            notification.save();

            if (null != jsonObject.getBoolean(GoodsMessageHelper.NewGoodsOrderType) &&
                    jsonObject.getBoolean(GoodsMessageHelper.NewGoodsOrderType)) { // 为true说明是商品下单的通知
                //打开自定义的Activity
                Intent i = new Intent(context, WelcomeActivity.class);
                i.putExtra(GoodsMessageHelper.NewGoodsOrderType, true); // 标志位
                i.putExtra(GoodsMessageHelper.GOODS_ID, jsonObject.getString(GoodsMessageHelper.GOODS_ID));
                i.putExtra(GoodsMessageHelper.MESSAGE_TIME, jsonObject.getLong(GoodsMessageHelper.MESSAGE_TIME));
                i.putExtra(GoodsMessageHelper.GOODS_BUYER_ID, jsonObject.getString(GoodsMessageHelper.GOODS_BUYER_ID));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } else if (null != jsonObject.getBoolean(GoodsMessageHelper.CHATTYPE) &&
                    jsonObject.getBoolean(GoodsMessageHelper.CHATTYPE)) { // 为true说明是商品评论回复的通知
                //打开自定义的Activity
                Intent i = new Intent(context, WelcomeActivity.class);
                i.putExtra(GoodsMessageHelper.CHATTYPE, true); // 让GoodsInfoActivity识别的标志位
                i.putExtra(GoodsMessageHelper.GOODS_ID, jsonObject.getString(GoodsMessageHelper.GOODS_ID));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } else if (null != jsonObject.getBoolean(GoodsMessageHelper.ConfirmGoodsOrderType) &&
                    jsonObject.getBoolean(GoodsMessageHelper.ConfirmGoodsOrderType)) { // 为true说明是确认商品订单的通知
                //打开自定义的Activity
                Intent i = new Intent(context, WelcomeActivity.class);
                i.putExtra(GoodsMessageHelper.ConfirmGoodsOrderType, true); // 标志位
                i.putExtra(GoodsMessageHelper.GOODS_ID, jsonObject.getString(GoodsMessageHelper.GOODS_ID));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } else if (null != jsonObject.getBoolean(GoodsMessageHelper.RefuseGoodsOrderType) &&
                    jsonObject.getBoolean(GoodsMessageHelper.RefuseGoodsOrderType)) { // 为true说明是拒绝商品订单的通知
                //打开自定义的Activity
                Intent i = new Intent(context, WelcomeActivity.class);
                i.putExtra(GoodsMessageHelper.RefuseGoodsOrderType, true); // 标志位
                i.putExtra(GoodsMessageHelper.GOODS_ID, jsonObject.getString(GoodsMessageHelper.GOODS_ID));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }
}
