package com.cose.easywu.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.gson.User;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.EditTextClearTools;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CheckVerifyCodeActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtCode;
    private ImageView mIvClearCode;
    private TextView mTvTitle;
    private Button mBtnNext;
    private ProgressBar mPb;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_verify_code);

        initView();
        initData();
    }

    private void initData() {
        // 设置输入框的清除监听
        EditTextClearTools.addClearListener(mEtCode, mIvClearCode);
        // 设置title的字体
        Typeface typeface = Typeface.createFromAsset(getAssets(),"hzgb.ttf");
        mTvTitle.setTypeface(typeface);
        // 设置email
        email = getIntent().getStringExtra("email");
    }

    private void initView() {
        mEtCode = findViewById(R.id.et_checkVerifyCode_code);
        mIvClearCode = findViewById(R.id.iv_checkVerifyCode_code_clear);
        mTvTitle = findViewById(R.id.tv_checkVerifyCode_title);
        mBtnNext = findViewById(R.id.btn_checkVerifyCode_next);
        mPb = findViewById(R.id.pb_checkVerifyCode);

        mBtnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_checkVerifyCode_next:
                checkVerifyCode();
                break;
            default:
        }
    }

    private void checkVerifyCode() {
        String code = mEtCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            ToastUtil.showMsg(this, "请输入验证码", Toast.LENGTH_SHORT);
            return;
        }
        mPb.setVisibility(View.VISIBLE);
        // 去服务器验证邮箱验证码
        User user = new User();
        user.setU_email(email);
        user.setU_code(code);
        String json = new Gson().toJson(user);
        String address = Constant.CHECKVERIFYCODE_URL;

        HttpUtil.sendPostRequest(address, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPb.setVisibility(View.GONE);
                        ToastUtil.showMsg(CheckVerifyCodeActivity.this, "验证失败", Toast.LENGTH_SHORT);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null == response.body()) {
                    return;
                }

                String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                final BaseMsg msg = Utility.handleBaseMsgResponse(responseText);
                if (null == msg) {
                    return;
                }
                if (msg.getCode().equals("0")) { // 验证码错误
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPb.setVisibility(View.GONE);
                            ToastUtil.showMsg(CheckVerifyCodeActivity.this, msg.getMsg(), Toast.LENGTH_SHORT);
                        }
                    });
                } else if (msg.getCode().equals("1")) { // 通过验证
                    // 界面跳转页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPb.setVisibility(View.GONE);
                            Intent intent = new Intent(CheckVerifyCodeActivity.this, ResetPwdActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }
}
