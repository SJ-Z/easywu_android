package com.cose.easywu.user.activity;

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
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.db.User;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.EditTextClearTools;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.Utility;
import com.google.gson.Gson;

import org.litepal.LitePal;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EditPwdActivity extends BaseActivity {

    private EditText mEtOldPwd, mEtNewPwd, mEtNewPwd2;
    private ImageView mIvClearOldPwd, mIvClearNewPwd, mIvClearNewPwd2;
    private TextView mTvTitle;
    private Button mBtnCommit;
    private ProgressBar mPb;

    private User user;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String newpwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pwd);

        initView();
        initData();
        setListener();
    }

    private void initData() {
        // 设置输入框的清除监听
        EditTextClearTools.addClearListener(mEtOldPwd, mIvClearOldPwd);
        EditTextClearTools.addClearListener(mEtNewPwd, mIvClearNewPwd);
        EditTextClearTools.addClearListener(mEtNewPwd2, mIvClearNewPwd2);
        // 设置title的字体
        Typeface typeface = Typeface.createFromAsset(getAssets(),"hzgb.ttf");
        mTvTitle.setTypeface(typeface);

        // 初始化pref和editor
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();

        // 初始化user
        String u_id = pref.getString("u_id", "");
        user = LitePal.where("u_id=?", u_id).findFirst(User.class);
    }

    private void initView() {
        mEtOldPwd = findViewById(R.id.et_editPwd_oldpwd);
        mEtNewPwd = findViewById(R.id.et_editPwd_newpwd);
        mEtNewPwd2 = findViewById(R.id.et_editPwd_newpwd2);
        mIvClearOldPwd = findViewById(R.id.iv_editPwd_oldpwd_clear);
        mIvClearNewPwd = findViewById(R.id.iv_editPwd_newpwd_clear);
        mIvClearNewPwd2 = findViewById(R.id.iv_editPwd_newpwd2_clear);
        mTvTitle = findViewById(R.id.tv_editPwd_title);
        mBtnCommit = findViewById(R.id.btn_editPwd_commit);
        mPb = findViewById(R.id.pb_editPwd);
    }

    private void setListener() {
        mBtnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPwd();
            }
        });
    }

    // 修改密码逻辑处理
    private void editPwd() {
        String oldpwd = mEtOldPwd.getText().toString();
        newpwd = mEtNewPwd.getText().toString();
        String newpwd2 = mEtNewPwd2.getText().toString();

        // 输入校验
        boolean correct = checkPwd(oldpwd, newpwd, newpwd2);
        if (!correct) {
            return;
        }
        // 去服务器校验并修改密码
        mPb.setVisibility(View.VISIBLE);
        editPwdOnServer();
    }

    // 去服务器校验并修改密码
    private void editPwdOnServer() {
        final com.cose.easywu.gson.User user = new com.cose.easywu.gson.User();
        user.setU_email(user.getU_email());
        user.setU_pwd(mEtNewPwd.getText().toString());
        user.setU_oldpwd(mEtOldPwd.getText().toString());

        String json = new Gson().toJson(user);
        String address = Constant.EDITPWD_URL;

        HttpUtil.sendPostRequest(address, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPb.setVisibility(View.GONE);
                        Toast.makeText(EditPwdActivity.this, "修改失败",
                                Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(EditPwdActivity.this, msg.getMsg(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (msg.getCode().equals("1")) { // 修改密码成功
                    // 页面更新
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPb.setVisibility(View.GONE);
                            editor.putString("pwd", newpwd);
                            editor.apply();
                            Toast.makeText(EditPwdActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    // 对密码做输入校验
    private boolean checkPwd(String oldpwd, String newpwd, String newpwd2) {
        if (TextUtils.isEmpty(oldpwd) || TextUtils.isEmpty(newpwd) || TextUtils.isEmpty(newpwd2)) {
            Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newpwd.equals(newpwd2)) {
            Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 验证密码格式
        // 验证原密码
        Pattern pattern = Pattern.compile("^[a-zA-Z]+\\w*");
        Matcher matcher = pattern.matcher(oldpwd);
        if (!matcher.matches()) {
            Toast.makeText(this, "原密码错误", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 验证密码长度
        if (oldpwd.length() < 6 || oldpwd.length() > 15) {
            Toast.makeText(this, "原密码错误", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 验证新密码
        matcher = pattern.matcher(newpwd);
        if (!matcher.matches()) {
            Toast.makeText(this, "密码必须以字母开头，且只能包含数字、字母、下划线", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 验证密码长度
        if (newpwd.length() < 6 || newpwd.length() > 15) {
            Toast.makeText(this, "密码长度必须为6~15", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
