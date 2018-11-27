package com.cose.easywu.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cose.easywu.R;
import com.cose.easywu.base.ActivityCollector;

public class ToastUtil {

    private static Toast mToast;
    private static Toast mToastOnCenter;
    private static Toast mImageToast;

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
        if (mImageToast != null) {
            mImageToast.cancel();
        }
        if (mToastOnCenter == null) {
            mToastOnCenter = Toast.makeText(context, msg, duration);
            mToastOnCenter.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToastOnCenter.setText(msg);
            mToastOnCenter.setDuration(duration);
        }
        mToastOnCenter.show();
    }

    public static void showImageToast(Context context, String msg, int resourseId, int duration) {
        if (mToastOnCenter != null) {
            mToastOnCenter.cancel();
        }
        if (mImageToast == null) {
            mImageToast = new Toast(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.layout_toast, null);
            ImageView imageView = view.findViewById(R.id.iv_toast);
            TextView textView = view.findViewById(R.id.tv_toast);
            imageView.setImageResource(resourseId);
            textView.setText(msg);
            mImageToast.setView(view);
        } else {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.layout_toast, null);
            ImageView imageView = view.findViewById(R.id.iv_toast);
            TextView textView = view.findViewById(R.id.tv_toast);
            imageView.setImageResource(resourseId);
            textView.setText(msg);
            mImageToast.setView(view);
        }
        mImageToast.setGravity(Gravity.CENTER, 0, 0);
        mImageToast.setDuration(duration);
        mImageToast.show();
    }
}
