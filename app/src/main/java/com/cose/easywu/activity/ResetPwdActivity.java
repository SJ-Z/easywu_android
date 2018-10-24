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
import com.cose.easywu.activityManage.ActivityCollector;
import com.cose.easywu.activityManage.BaseActivity;
import com.cose.easywu.utils.EditTextClearTools;

public class ResetPwdActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtPwd;
    private ImageView mIvClearPwd;
    private EditText mEtPwd2;
    private ImageView mIvClearPwd2;
    private TextView mTvTitle;
    private TextView mTvReturnLogin;
    private Button mBtnCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);

        initView();
        initData();
    }

    private void initData() {
        // 设置输入框的清除监听
        EditTextClearTools.addClearListener(mEtPwd, mIvClearPwd);
        EditTextClearTools.addClearListener(mEtPwd2, mIvClearPwd2);
        // 设置title的字体
        Typeface typeface = Typeface.createFromAsset(getAssets(),"hzgb.ttf");
        mTvTitle.setTypeface(typeface);
    }

    private void initView() {
        mEtPwd = findViewById(R.id.et_resetPwd_pwd);
        mIvClearPwd = findViewById(R.id.iv_resetPwd_pwd_clear);
        mEtPwd2 = findViewById(R.id.et_resetPwd_pwd2);
        mIvClearPwd2 = findViewById(R.id.iv_resetPwd_pwd_clear2);
        mTvTitle = findViewById(R.id.tv_resetPwd_title);
        mTvReturnLogin = findViewById(R.id.tv_resetPwd_returnLogin);
        mBtnCommit = findViewById(R.id.btn_resetPwd_commit);

        mTvReturnLogin.setOnClickListener(this);
        mBtnCommit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tv_resetPwd_returnLogin:
                ActivityCollector.finishAll();
                intent = new Intent(ResetPwdActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_resetPwd_commit:

                break;
            default:
        }
    }
}
