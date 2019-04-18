package com.cose.easywu.user.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.db.User;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.NoEmojiEditText;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;

import org.litepal.LitePal;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SendAdviseActivity extends BaseActivity {

    private ImageView mIvBack;
    private TextView mTvNumCount;
    private NoEmojiEditText mEtDesc;
    private Button mBtnSubmit;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_advise);

        initView();
        initListener();
    }

    private void initListener() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mEtDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTvNumCount.setText(String.valueOf(s.length()));
                if(!TextUtils.isEmpty(s)){
                    mBtnSubmit.setEnabled(true);
                    mBtnSubmit.setBackgroundColor(Color.parseColor("#FF4200"));
                    mBtnSubmit.setTextColor(Color.WHITE);
                } else {
                    mBtnSubmit.setEnabled(false);
                    mBtnSubmit.setBackgroundColor(Color.parseColor("#DADADA"));
                    mBtnSubmit.setTextColor(Color.parseColor("#909090"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mEtDesc.getText().toString().trim();
                if(!TextUtils.isEmpty(content)) {
                    sendAdviseToServer(content);
                } else {
                    Toast.makeText(SendAdviseActivity.this,"内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 向服务器写入反馈内容
    private void sendAdviseToServer(String content) {
        showProgressDialog();
        String u_id = PreferenceManager.getDefaultSharedPreferences(this).getString("u_id", "");
        String u_nick = LitePal.where("u_id=?", u_id).findFirst(User.class).getU_nick();
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        jsonObject.put("content", content);
        jsonObject.put("u_id", u_id);
        jsonObject.put("u_nick", u_nick);
        String address = Constant.ADD_ADVISE_URL;

        HttpUtil.sendPostRequest(address, jsonObject.toString(), new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        ToastUtil.showMsgOnCenter(SendAdviseActivity.this, "反馈失败，请检查网络状态", Toast.LENGTH_SHORT);
                        Log.e("SendAdviseActivity", "反馈失败==" + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null == response.body()) {
                    return;
                }
                // 解析数据
                String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                final BaseMsg msg = Utility.handleBaseMsgResponse(responseText);
                if (null == msg) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        if ("1".equals(msg.getCode())) {
                            finish();
                            ToastUtil.showMsgOnCenter(SendAdviseActivity.this, "反馈成功", Toast.LENGTH_SHORT);
                            Log.d("SendAdviseActivity", "反馈成功");
                        } else {
                            ToastUtil.showMsgOnCenter(SendAdviseActivity.this, "反馈失败", Toast.LENGTH_SHORT);
                            Log.d("SendAdviseActivity", "反馈失败");
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        mIvBack = findViewById(R.id.iv_advise_back);
        mTvNumCount = findViewById(R.id.tv_advise_count);
        mEtDesc = findViewById(R.id.et_advise_desc);
        mBtnSubmit = findViewById(R.id.btn_advise_submit);
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在发布中，请等待...");
        }
        progressDialog.show();
    }
}
