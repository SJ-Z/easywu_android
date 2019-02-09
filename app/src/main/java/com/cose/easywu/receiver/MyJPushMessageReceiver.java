package com.cose.easywu.receiver;

import android.content.Context;
import android.util.Log;

import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.service.JPushMessageReceiver;

public class MyJPushMessageReceiver extends JPushMessageReceiver {

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);
        Log.e("-----", "sequence:" + jPushMessage.getSequence() +
        ", errorcode:" + jPushMessage.getErrorCode() + ", alias:" + jPushMessage.getAlias());
    }
}
