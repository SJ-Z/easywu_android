package com.cose.easywu.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {

    public static Toast mToast;
    public static Toast mToastOnCenter;

    public static void showMsg(Context context, String msg, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, duration);
        } else {
            mToast.setText(msg);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    public static void showMsgOnCenter(Context context, String msg, int duration) {
        if (mToastOnCenter == null) {
            mToastOnCenter = Toast.makeText(context, msg, duration);
            mToastOnCenter.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToastOnCenter.setText(msg);
            mToastOnCenter.setDuration(duration);
        }
        mToastOnCenter.show();
    }
}
