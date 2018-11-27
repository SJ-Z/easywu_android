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

public class RegistActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtEmail;
    private EditText mEtNick;
    private EditText mEtPwd;
    private EditText mEtPwd2;
    private ImageView mIvClearEmail;
    private ImageView mIvClearNick;
    private ImageView mIvClearPwd;
    private ImageView mIvClearPwd2;
    private TextView mTvTitle;
    private TextView mTvReturnLogin;
    private Button mBtnRegist;

    private ProgressDialog progressDialog;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        
        initView();
        initData();
    }

    private void initData() {
        // 设置输入框的清除监听
        EditTextClearTools.addClearListener(mEtEmail, mIvClearEmail);
        EditTextClearTools.addClearListener(mEtNick, mIvClearNick);
        EditTextClearTools.addClearListener(mEtPwd, mIvClearPwd);
        EditTextClearTools.addClearListener(mEtPwd2, mIvClearPwd2);
        // 设置title的字体
        Typeface typeface = Typeface.createFromAsset(getAssets(),"hzgb.ttf");
        mTvTitle.setTypeface(typeface);

        editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
    }

    private void initView() {
        mEtEmail = findViewById(R.id.et_regist_email);
        mEtNick = findViewById(R.id.et_regist_nick);
        mEtPwd = findViewById(R.id.et_regist_pwd);
        mEtPwd2 = findViewById(R.id.et_regist_pwd2);
        mIvClearEmail = findViewById(R.id.iv_regist_email_clear);
        mIvClearNick = findViewById(R.id.iv_regist_nick_clear);
        mIvClearPwd = findViewById(R.id.iv_regist_pwd_clear);
        mIvClearPwd2 = findViewById(R.id.iv_regist_pwd_clear2);
        mTvTitle = findViewById(R.id.tv_regist_title);
        mTvReturnLogin = findViewById(R.id.tv_regist_returnLogin);
        mBtnRegist = findViewById(R.id.btn_regist_regist);

        mTvReturnLogin.setOnClickListener(this);
        mBtnRegist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tv_regist_returnLogin:
                intent = new Intent(RegistActivity.this, LoginActivity.class);
                ActivityCollector.finishAll(); // 销毁所有活动
                startActivity(intent); // 重启登录活动
                break;
            case R.id.btn_regist_regist:
                showProgressDialog();
                regist();
                break;
            default:
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

    // 注册逻辑实现
    private void regist() {
        if (!checkRegistInfo()) { // 注册失败
            progressDialog.dismiss();
            return;
        }
        // 去服务器注册
        registOnServer();
    }

    // 去服务器注册
    private void registOnServer() {
        String email = mEtEmail.getText().toString().trim();
        String nick = mEtNick.getText().toString().trim();
        String pwd = mEtPwd.getText().toString().trim();
        User user = new User(email, pwd, nick);
        String json = new Gson().toJson(user);
        String address = Constant.REGIST_URL;

        HttpUtil.sendPostRequest(address, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        ToastUtil.showMsgOnCenter(RegistActivity.this, "注册失败", Toast.LENGTH_SHORT);
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
                if (msg.getCode().equals("0")) { // 注册失败
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            ToastUtil.showMsgOnCenter(RegistActivity.this, msg.getMsg(), Toast.LENGTH_SHORT);
                        }
                    });
                } else if (msg.getCode().equals("1")) { // 注册成功
                    // 将注册邮箱、密码写入sd卡
                    editor.putString("email", mEtEmail.getText().toString().trim());
                    editor.putString("pwd", mEtPwd.getText().toString().trim());
                    editor.putBoolean("autoLogin", false);
                    editor.apply();

                    // 界面跳转到登录页面，提示注册成功
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            startActivity(new Intent(RegistActivity.this, LoginActivity.class));
                            finish();
                            ToastUtil.showMsgOnCenter(RegistActivity.this, "注册成功！赶紧去邮箱激活吧！",
                                    Toast.LENGTH_SHORT);
                        }
                    });
                }
            }
        });
    }

    // 校验注册信息是否合法
    private boolean checkRegistInfo() {
        String email = mEtEmail.getText().toString().trim();
        String nick = mEtNick.getText().toString().trim();
        String pwd = mEtPwd.getText().toString().trim();
        String pwd2 = mEtPwd2.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            ToastUtil.showMsg(this, "邮箱不能为空", Toast.LENGTH_SHORT);
            return false;
        } else if (TextUtils.isEmpty(nick)) {
            ToastUtil.showMsg(this, "昵称不能为空", Toast.LENGTH_SHORT);
            return false;
        } else if (TextUtils.isEmpty(pwd)) {
            ToastUtil.showMsg(this, "密码不能为空", Toast.LENGTH_SHORT);
            return false;
        } else if (TextUtils.isEmpty(pwd2)) {
            ToastUtil.showMsg(this, "重复密码不能为空", Toast.LENGTH_SHORT);
            return false;
        }
        if (!pwd.equals(pwd2)) { // 两次密码输入不一致
            ToastUtil.showMsg(this, "两次密码输入不一致", Toast.LENGTH_SHORT);
            return false;
        }
        // 验证邮箱格式
        if (email.length() > 30) {
            ToastUtil.showMsg(this, "邮箱长度非法", Toast.LENGTH_SHORT);
            return false;
        }
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z]+\\w*@([0-9a-zA-Z]+\\.)+[0-9a-zA-Z]+$");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            ToastUtil.showMsg(this, "邮箱格式非法", Toast.LENGTH_SHORT);
            return false;
        }

        // 验证昵称长度
        if (nick.length() > 7) {
            ToastUtil.showMsg(this, "昵称长度必须小于7", Toast.LENGTH_SHORT);
            return false;
        }

        // 验证密码格式
        pattern = Pattern.compile("^[a-zA-Z]+\\w*");
        matcher = pattern.matcher(pwd);
        if (!matcher.matches()) {
            ToastUtil.showMsg(this, "密码必须以字母开头，且只能包含数字、字母、下划线", Toast.LENGTH_SHORT);
            return false;
        }

        // 验证密码长度
        if (pwd.length() < 6 || pwd.length() > 15) {
            ToastUtil.showMsg(this, "密码长度必须为6~15", Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }
}
