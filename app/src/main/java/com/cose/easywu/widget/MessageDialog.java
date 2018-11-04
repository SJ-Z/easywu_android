package com.cose.easywu.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.cose.easywu.R;

public class MessageDialog extends Dialog implements View.OnClickListener {

    private TextView mTvTitle, mTvContent, mTvCancel, mTvConfirm;
    private String title, content, cancel, confirm;
    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;

    public MessageDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public MessageDialog setContent(String content) {
        this.content = content;
        return this;
    }

    public MessageDialog setCancel(String cancel, IOnCancelListener cancelListener) {
        this.cancel = cancel;
        this.cancelListener = cancelListener;
        return this;
    }

    public MessageDialog setConfirm(String confirm, IOnConfirmListener confirmListener) {
        this.confirm = confirm;
        this.confirmListener = confirmListener;
        return this;
    }

    public MessageDialog(@NonNull Context context) {
        super(context);
    }

    public MessageDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_message_dialog);

        //设置宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int)(size.x * 0.8); //设置dialog的宽度为当前手机屏幕的宽度*0.8
        getWindow().setAttributes(p);

        mTvTitle = findViewById(R.id.tv_message_title);
        mTvContent = findViewById(R.id.tv_message_content);
        mTvCancel = findViewById(R.id.tv_message_cancel);
        mTvConfirm = findViewById(R.id.tv_message_confirm);

        if (!TextUtils.isEmpty((title))) {
            mTvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(content)) {
            mTvContent.setText(content);
        }
        if (!TextUtils.isEmpty(cancel)) {
            mTvCancel.setText(cancel);
        }
        if (!TextUtils.isEmpty(confirm)) {
            mTvConfirm.setText(confirm);
        }

        mTvCancel.setOnClickListener(this);
        mTvConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_message_cancel:
                if (cancelListener != null) {
                    cancelListener.onCancel(this);
                }
                dismiss();
                break;
            case R.id.tv_message_confirm:
                if (confirmListener != null) {
                    confirmListener.onConfirm(this);
                }
                dismiss();
                break;
        }
    }

    public interface IOnCancelListener {
        void onCancel(MessageDialog dialog);
    }

    public interface IOnConfirmListener {
        void onConfirm(MessageDialog dialog);
    }
}
