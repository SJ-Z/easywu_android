package com.cose.easywu.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cose.easywu.R;
import com.cose.easywu.base.ActivityCollector;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.EditTextClearTools;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.NoEmojiEditText;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FindPwdActivity extends BaseActivity implements View.OnClickListener {

    private NoEmojiEditText mEtEmail;
    private ImageView mIvClearEmail;
    private TextView mTvTitle;
    private TextView mTvReturnLogin;
    private Button mBtnNext;
    private ProgressBar mPb;

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
        mPb = findViewById(R.id.pb_findPwd);

        mTvReturnLogin.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tv_findPwd_returnLogin:
                intent = new Intent(FindPwdActivity.this, LoginActivity.class);
                ActivityCollector.finishAll(); // 销毁所有活动
                startActivity(intent); // 重启登录活动
                break;
            case R.id.btn_findPwd_next:
                goNext();
                break;
            default:
        }
    }

    // 处理下一步的逻辑
    private void goNext() {
        String email = mEtEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            ToastUtil.showMsg(this, "邮箱不能为空", Toast.LENGTH_SHORT);
            return;
        }
        // 验证邮箱格式
        if (email.length() > 30) {
            ToastUtil.showMsg(this, "邮箱不能为空", Toast.LENGTH_SHORT);
            return;
        }
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z]+\\w*@([0-9a-zA-Z]+\\.)+[0-9a-zA-Z]+$");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            ToastUtil.showMsg(this, "邮箱格式错误", Toast.LENGTH_SHORT);
            return;
        }

        // 显示进度条
        mPb.setVisibility(View.VISIBLE);
        // 去服务器验证邮箱
        verifyFromServer(email);
    }

    // 去服务器验证邮箱
    private void verifyFromServer(final String email) {
        String address = Constant.FINDPWD_URL;

        HttpUtil.sendPostRequest(address, email, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPb.setVisibility(View.GONE);
                        ToastUtil.showMsg(FindPwdActivity.this, "验证失败", Toast.LENGTH_SHORT);
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
                if (msg.getCode().equals("0")) { // 邮箱不存在
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPb.setVisibility(View.GONE);
                            ToastUtil.showMsg(FindPwdActivity.this, msg.getMsg(), Toast.LENGTH_SHORT);
                        }
                    });
                } else if (msg.getCode().equals("1")) { // 邮箱存在
                    // 界面跳转页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPb.setVisibility(View.GONE);
                            Intent intent = new Intent(FindPwdActivity.this, CheckVerifyCodeActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }
}
