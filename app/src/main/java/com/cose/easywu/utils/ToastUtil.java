package com.cose.easywu.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static Toast mToast;
    public static void showMsg(Context context, String msg, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, duration);
        } else {
            mToast.setText(msg);
            mToast.setDuration(duration);
        }
        mToast.show();
    }
}
