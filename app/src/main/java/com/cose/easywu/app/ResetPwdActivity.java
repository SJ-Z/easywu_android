package com.cose.easywu.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cose.easywu.R;
import com.cose.easywu.base.ActivityCollector;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ResetPwdActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtPwd;
    private ImageView mIvClearPwd;
    private EditText mEtPwd2;
    private ImageView mIvClearPwd2;
    private TextView mTvTitle;
    private TextView mTvReturnLogin;
    private Button mBtnCommit;
    private ProgressBar mPb;

    private String email;
    private SharedPreferences.Editor editor;

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
        // 获取email
        email = getIntent().getStringExtra("email");
        // 初始化editor
        editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
    }

    private void initView() {
        mEtPwd = findViewById(R.id.et_resetPwd_pwd);
        mIvClearPwd = findViewById(R.id.iv_resetPwd_pwd_clear);
        mEtPwd2 = findViewById(R.id.et_resetPwd_pwd2);
        mIvClearPwd2 = findViewById(R.id.iv_resetPwd_pwd_clear2);
        mTvTitle = findViewById(R.id.tv_resetPwd_title);
        mTvReturnLogin = findViewById(R.id.tv_resetPwd_returnLogin);
        mBtnCommit = findViewById(R.id.btn_resetPwd_commit);
        mPb = findViewById(R.id.pb_resetPwd);

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
                resetPwd();
                break;
            default:
        }
    }

    private void resetPwd() {
        final String pwd = mEtPwd.getText().toString().trim();
        String pwd2 = mEtPwd2.getText().toString().trim();
        // 验证密码是否合理
        boolean correct = checkPwd(pwd, pwd2);
        if (!correct) {
            return;
        }
        mPb.setVisibility(View.VISIBLE);
        // 去服务器修改密码
        User user = new User();
        user.setU_email(email);
        user.setU_pwd(pwd);
        String json = new Gson().toJson(user);
        String address = Constant.RESETPWD_URL;

        HttpUtil.sendPostRequest(address, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPb.setVisibility(View.GONE);
                        ToastUtil.showMsg(ResetPwdActivity.this, "修改失败", Toast.LENGTH_SHORT);
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
                if (msg.getCode().equals("0")) { // 修改密码失败
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPb.setVisibility(View.GONE);
                            ToastUtil.showMsg(ResetPwdActivity.this, msg.getMsg(), Toast.LENGTH_SHORT);
                        }
                    });
                } else if (msg.getCode().equals("1")) { // 修改密码成功
                    // 界面跳转页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPb.setVisibility(View.GONE);
                            editor.putString("email", email);
                            editor.putString("pwd", pwd);
                            editor.apply();
                            ActivityCollector.finishAll();
                            startActivity(new Intent(ResetPwdActivity.this, LoginActivity.class));
                            ToastUtil.showMsg(ResetPwdActivity.this, "修改密码成功，赶紧登录吧", Toast.LENGTH_SHORT);
                        }
                    });
                }
            }
        });
    }

    private boolean checkPwd(String pwd, String pwd2) {
        if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd2)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!pwd.equals(pwd2)) {
            Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 验证密码格式
        Pattern pattern = Pattern.compile("^[a-zA-Z]+\\w*");
        Matcher matcher = pattern.matcher(pwd);
        if (!matcher.matches()) {
            Toast.makeText(this, "密码必须以字母开头，且只能包含数字、字母、下划线", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 验证密码长度
        if (pwd.length() < 6 || pwd.length() > 15) {
            Toast.makeText(this, "密码长度必须为6~15", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
