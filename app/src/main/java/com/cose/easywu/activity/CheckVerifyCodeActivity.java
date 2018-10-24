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

public class CheckVerifyCodeActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtEmail;
    private ImageView mIvClearEmail;
    private TextView mTvTitle;
    private Button mBtnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_verify_code);

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
        mEtEmail = findViewById(R.id.et_checkVerifyCode_email);
        mIvClearEmail = findViewById(R.id.iv_checkVerifyCode_email_clear);
        mTvTitle = findViewById(R.id.tv_checkVerifyCode_title);
        mBtnNext = findViewById(R.id.btn_checkVerifyCode_next);

        mBtnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_checkVerifyCode_next:
                Intent intent = new Intent(CheckVerifyCodeActivity.this, ResetPwdActivity.class);
                startActivity(intent);
                break;
            default:
        }
    }
}
