package com.cose.easywu.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cose.easywu.R;
import com.cose.easywu.activityManage.BaseActivity;
import com.cose.easywu.utils.EditTextClearTools;

public class FindPwdActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtEmail;
    private ImageView mIvClearEmail;
    private TextView mTvTitle;
    private TextView mTvReturnLogin;
    private Button mBtnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);

        initView();
        initData();
    }

    private void initData() {
        // 设置输入框的清除监听
        EditTextClearTools.addClearListener(mEtEmail, mIvClearEmail);
        // 设置title的字体
        Typeface typeface = Typeface.createFromAsset(getAssets(),"hzgb.ttf");
        mTvTitle.setTypeface(typeface);
    }

    private void initView() {
        mEtEmail = findViewById(R.id.et_findPwd_email);
        mIvClearEmail = findViewById(R.id.iv_findPwd_email_clear);
        mTvTitle = findViewById(R.id.tv_findPwd_title);
        mTvReturnLogin = findViewById(R.id.tv_findPwd_returnLogin);
        mBtnNext = findViewById(R.id.btn_findPwd_next);

        mTvReturnLogin.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tv_findPwd_returnLogin:
                intent = new Intent(FindPwdActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_findPwd_next:
                intent = new Intent(FindPwdActivity.this, CheckVerifyCodeActivity.class);
                startActivity(intent);
                break;
            default:
        }
    }
}
