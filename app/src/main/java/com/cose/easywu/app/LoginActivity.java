package com.cose.easywu.app;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.cose.easywu.utils.NoEmojiEditText;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private NoEmojiEditText mEtEmail;
    private NoEmojiEditText mEtPwd;
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
            checkPhotoPermission();
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
                checkPhotoPermission();
                break;
            default:
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    // 检查获取读写外部存储的权限
    private void checkPhotoPermission() {
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            login();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    login();
                } else {
                    ToastUtil.showMsgOnCenter(this, "权限被拒绝，程序将无法运行", Toast.LENGTH_SHORT);
                }
                break;
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
        showProgressDialog();
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

                    EMClient.getInstance().login(loginMsg.getU_id(), loginMsg.getU_id(), new EMCallBack() {
                        /**
                         * 登陆成功的回调
                         */
                        @Override
                        public void onSuccess() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (progressDialog != null) {
                                        progressDialog.dismiss();
                                    }

                                    // 加载所有会话到内存
                                    EMClient.getInstance().chatManager().loadAllConversations();
                                    // 加载所有群组到内存，如果使用了群组的话
                                    // EMClient.getInstance().groupManager().loadAllGroups();

                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                            });
                        }

                        /**
                         * 登陆错误的回调
                         * @param i
                         * @param s
                         */
                        @Override
                        public void onError(final int i, final String s) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (progressDialog != null) {
                                        progressDialog.dismiss();
                                    }
                                    Log.d("LoginActivity", "登录失败 Error code:" + i + ", message:" + s);
                                    /**
                                     * 关于错误码可以参考官方api详细说明
                                     * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                                     */
                                    switch (i) {
                                        // 网络异常 2
                                        case EMError.NETWORK_ERROR:
                                            Toast.makeText(LoginActivity.this, "网络错误 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                            break;
                                        // 无效的用户名 101
                                        case EMError.INVALID_USER_NAME:
                                            Toast.makeText(LoginActivity.this, "无效的用户名 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                            break;
                                        // 无效的密码 102
                                        case EMError.INVALID_PASSWORD:
                                            Toast.makeText(LoginActivity.this, "无效的密码 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                            break;
                                        // 用户认证失败，用户名或密码错误 202
                                        case EMError.USER_AUTHENTICATION_FAILED:
                                            Toast.makeText(LoginActivity.this, "用户认证失败，用户名或密码错误 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                            break;
                                        // 用户不存在 204
                                        case EMError.USER_NOT_FOUND:
                                            Toast.makeText(LoginActivity.this, "用户不存在 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                            break;
                                        // 无法访问到服务器 300
                                        case EMError.SERVER_NOT_REACHABLE:
                                            Toast.makeText(LoginActivity.this, "无法访问到服务器 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                            break;
                                        // 等待服务器响应超时 301
                                        case EMError.SERVER_TIMEOUT:
                                            Toast.makeText(LoginActivity.this, "等待服务器响应超时 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                            break;
                                        // 服务器繁忙 302
                                        case EMError.SERVER_BUSY:
                                            Toast.makeText(LoginActivity.this, "服务器繁忙 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                            break;
                                        // 未知 Server 异常 303 一般断网会出现这个错误
                                        case EMError.SERVER_UNKNOWN_ERROR:
                                            Toast.makeText(LoginActivity.this, "未知的服务器异常 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                            break;
                                        default:
                                            Toast.makeText(LoginActivity.this, "ml_sign_in_failed code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                            break;
                                    }
                                }
                            });
                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                }
            }
        });
    }
}
