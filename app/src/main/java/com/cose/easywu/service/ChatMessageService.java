package com.cose.easywu.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.cose.easywu.R;
import com.cose.easywu.app.MainActivity;
import com.cose.easywu.base.ActivityCollector;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseUI;

import java.util.List;

public class ChatMessageService extends Service {
    private ChatMessageBinder mBinder = new ChatMessageBinder();
    public static int newMsgCode = 1;

    public ChatMessageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification() {
        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("chatMsg", true);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, null);
        builder.setSmallIcon(R.mipmap.ic_logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_logo));
        builder.setContentIntent(pi);
        builder.setContentTitle("简物");
        builder.setContentText("新消息提醒");
        return builder.build();
    }

    public class ChatMessageBinder extends Binder {
        // 开始监听
        public void startListen() {
            // 监听会话消息
            EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
        }
    }

    private EMMessageListener emMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            getNotificationManager().notify(newMsgCode, getNotification());
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageRead(List<EMMessage> list) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {

        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };
}
