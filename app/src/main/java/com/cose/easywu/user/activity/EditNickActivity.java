package com.cose.easywu.user.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.base.MyApplication;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EditNickActivity extends BaseActivity {

    private ImageView mIvBack, mIvClear;
    private TextView mTvSave;
    private EditText mEtNick;

    private User user;
    private String oldNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_nick);

        initView();
        initData();
        initListener();
    }

    private void initListener() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newNick = mEtNick.getText().toString().trim();
                if (newNick.equals(oldNick)) { // 未修改
                    finish(); // 关闭当前活动
                    return;
                }
                if (!checkNick(newNick)) { // 昵称不合法
                    return;
                }
                // 去服务器校验并修改昵称
                editNickOnServer(newNick);
            }
        });
    }

    // 去服务器修改昵称
    private void editNickOnServer(final String newNick) {
        com.cose.easywu.gson.User user = new com.cose.easywu.gson.User();
        user.setU_id(this.user.getU_id());
        user.setU_nick(newNick);

        String json = new Gson().toJson(user);
        String address = Constant.EDITNICK_URL;

        HttpUtil.sendPostRequest(address, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EditNickActivity.this, "昵称修改失败",
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
                if (msg.getCode().equals("0")) { // 昵称修改失败
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditNickActivity.this, msg.getMsg(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (msg.getCode().equals("1")) { // 昵称修改成功
                    // 更新本地数据库
                    editLocalDBNick(newNick);
                    // 关闭页面
                    finish();
                }
            }
        });
    }

    // 更新本地数据库
    private void editLocalDBNick(String newNick) {
        user.setU_nick(newNick);
        user.save();
    }

    // 校验昵称是否合法
    private boolean checkNick(String newNick) {
        if (TextUtils.isEmpty(newNick)) {
            Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 验证昵称长度
        if (newNick.length() > 7) {
            Toast.makeText(this, "昵称长度必须小于7", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initData() {
        // 设置输入框的清除监听
        EditTextClearTools.addClearListener(mEtNick, mIvClear);

        user = LitePal.findFirst(User.class);

        oldNick = user.getU_nick();
        mEtNick.setText(oldNick);

        // 设置光标位置在昵称末尾
        mEtNick.setSelection(oldNick.length());
    }

    private void initView() {
        mIvBack = findViewById(R.id.iv_editNick_back);
        mIvClear = findViewById(R.id.iv_editNick_clear);
        mTvSave = findViewById(R.id.tv_editNick_save);
        mEtNick = findViewById(R.id.et_editNick_nick);
    }
}
