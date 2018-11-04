package com.cose.easywu.app;

import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.gson.User;
import com.cose.easywu.gson.msg.LoginMsg;
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

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtEmail;
    private EditText mEtPwd;
    private ImageView mIvClearEmail;
    private ImageView mIvClearPwd;
    private ImageView mIvIcon;
    private TextView mTvTitle;
    private TextView mTvForgetPwd;
    private TextView mTvRegist;
    private Button mBtnLogin;

    private ProgressDialog progressDialog;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initData();
    }

    private void initData() {
        // 设置输入框的清除监听
        EditTextClearTools.addClearListener(mEtEmail, mIvClearEmail);
        EditTextClearTools.addClearListener(mEtPwd, mIvClearPwd);
        // 设置title的字体
        Typeface typeface = Typeface.createFromAsset(getAssets(),"hzgb.ttf");
        mTvTitle.setTypeface(typeface);
        // 设置自动输入用户名、密码
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String email = pref.getString("email", "");
        String pwd = pref.getString("pwd", "");
        mEtEmail.setText(email);
        mEtPwd.setText(pwd);
        // 初始化editor
        editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        // 是否直接登录
        boolean autoLogin = pref.getBoolean("autoLogin", false);
        if (autoLogin) {
            login();
        }
    }

    private void initView() {
        mEtEmail = findViewById(R.id.et_login_email);
        mEtPwd = findViewById(R.id.et_login_pwd);
        mIvClearEmail = findViewById(R.id.iv_login_email_clear);
        mIvClearPwd = findViewById(R.id.iv_login_pwd_clear);
        mIvIcon = findViewById(R.id.iv_login_icon);
        mTvTitle = findViewById(R.id.tv_login_title);
        mTvForgetPwd = findViewById(R.id.tv_login_forgetPwd);
        mTvRegist = findViewById(R.id.tv_login_regist);
        mBtnLogin = findViewById(R.id.btn_login_login);

        mTvForgetPwd.setOnClickListener(this);
        mTvRegist.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tv_login_forgetPwd:
                intent = new Intent(LoginActivity.this, FindPwdActivity.class);
                break;
            case R.id.tv_login_regist:
                intent = new Intent(LoginActivity.this, RegistActivity.class);
                break;
            case R.id.btn_login_login:
                showProgressDialog();
                login();
                break;
            default:
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在验证...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    // 登录逻辑实现
    private void login() {
        String email = mEtEmail.getText().toString().trim();
        String pwd = mEtPwd.getText().toString().trim();
        // 判断输入是否为空
        if (TextUtils.isEmpty(email)) {
            ToastUtil.showMsg(this, "邮箱输入不能为空", Toast.LENGTH_SHORT);
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            ToastUtil.showMsg(this, "密码输入不能为空", Toast.LENGTH_SHORT);
            return;
        }
        // 去服务器验证账号信息
        loginOnServer();
    }

    // 去服务器验证账号信息
    private void loginOnServer() {
        final String email = mEtEmail.getText().toString().trim();
        final String pwd = mEtPwd.getText().toString().trim();
        final User user = new User(email, pwd);
        String json = new Gson().toJson(user);
        String address = Constant.LOGIN_URL;

        HttpUtil.sendPostRequest(address, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        ToastUtil.showMsg(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null == response.body()) {
                    return;
                }

                String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                final LoginMsg loginMsg = Utility.handleLoginResponse(responseText);
                if (null == loginMsg) {
                    return;
                }
                if (loginMsg.getCode().equals("0")) { // 登录失败
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            ToastUtil.showMsg(LoginActivity.this, loginMsg.getMsg(), Toast.LENGTH_SHORT);
                        }
                    });
                } else if (loginMsg.getCode().equals("1")) { // 登录成功
                    // 将用户id及自动登录写入SharedPreferences
                    editor.putString("u_id", loginMsg.getU_id());
                    editor.putString("email", email);
                    editor.putString("pwd", pwd);
                    editor.putBoolean("autoLogin", true);
                    editor.apply();

                    // 界面跳转到主页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    });
                }
            }
        });
    }
}
