package com.cose.easywu.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.base.MyApplication;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // 两秒钟进入应用
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 执行在主线程
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
                boolean autoEnter = false; // 是否已登录，为true直接进入主页面
                String u_id = pref.getString("u_id", "");
                if (!TextUtils.isEmpty(u_id)) {
                    autoEnter = true;
                }
                // 启动主页面
                if (autoEnter) {
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    intent.putExtra("chat", getIntent().getBooleanExtra("chat", false));
                    startActivity(intent);
                } else {
                    // 启动登录页面
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                }
                // 关闭当前页面
                finish();
            }
        }, 2000);
    }
}
