package com.cose.easywu.user.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.db.User;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ImageUtils;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;
import com.google.gson.Gson;

import org.litepal.LitePal;

import java.io.IOException;
import java.net.URLDecoder;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EditUserInfoActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mIvBack;
    private LinearLayout mLlPhoto, mLlNick, mLlSex;
    private CircleImageView mIvPhoto;
    private TextView mTvNick, mTvEmail, mTvUid, mTvSex, mTvEditPwd;

    private User user;

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);

        initView();
        initData();
    }

    private void initData() {
        String u_id = PreferenceManager.getDefaultSharedPreferences(this).getString("u_id", "");
        user = LitePal.where("u_id=?", u_id).findFirst(User.class);

        if (!TextUtils.isEmpty(user.getU_photo())) {
            Bitmap bitmap = ImageUtils.getPhotoFromStorage(user.getU_id());
            Glide.with(this).load(bitmap)
                    .apply(new RequestOptions().placeholder(R.drawable.nav_icon))
                    .into(mIvPhoto);
        }
        mTvNick.setText(user.getU_nick());
        mTvEmail.setText(user.getU_email());
        mTvUid.setText(user.getU_id());
        mTvSex.setText(user.getU_sex()==0?"女":"男");
    }

    private void initView() {
        mIvBack = findViewById(R.id.iv_userinfo_back);
        mLlPhoto = findViewById(R.id.ll_userinfo_photo);
        mLlNick = findViewById(R.id.ll_userinfo_nick);
        mLlSex = findViewById(R.id.ll_userinfo_sex);
        mIvPhoto = findViewById(R.id.iv_user_photo);
        mTvNick = findViewById(R.id.tv_userinfo_nick);
        mTvEmail = findViewById(R.id.tv_userinfo_email);
        mTvUid = findViewById(R.id.tv_userinfo_uid);
        mTvSex = findViewById(R.id.tv_userinfo_sex);
        mTvEditPwd = findViewById(R.id.tv_userinfo_editpwd);

        mIvBack.setOnClickListener(this);
        mTvEditPwd.setOnClickListener(this);
        mLlPhoto.setOnClickListener(this);
        mLlNick.setOnClickListener(this);
        mLlSex.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_userinfo_back:
                finish();
                break;
            case R.id.tv_userinfo_editpwd:
                startActivity(new Intent(EditUserInfoActivity.this, EditPwdActivity.class));
                break;
            case R.id.ll_userinfo_photo:
                startActivity(new Intent(EditUserInfoActivity.this, EditPhotoActivity.class));
                break;
            case R.id.ll_userinfo_nick:
                startActivity(new Intent(EditUserInfoActivity.this, EditNickActivity.class));
                break;
            case R.id.ll_userinfo_sex:
                chooseSex();
                break;
        }
    }

    // 选择性别
    private void chooseSex() {
        final int originSex = user.getU_sex();
        final String[] sexArr = new String[]{"女", "男"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditUserInfoActivity.this);
        builder.setTitle("选择性别").setSingleChoiceItems(sexArr, originSex,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //关闭dialog
                        dialog.dismiss();

                        if (originSex == which) { // 用户未做修改，直接返回
                            return;
                        }
                        // 去服务器修改性别，修改完成后，更新本地数据库
                        editSexToServer(which);
                    }
                }).show();
    }

    // 修改本地数据库的用户性别
    private void editLocalDBSex(int sex) {
        user.setU_sex(sex);
        user.save();
    }

    // 去服务器修改性别
    private void editSexToServer(final int sex) {
        com.cose.easywu.gson.User user = new com.cose.easywu.gson.User();
        user.setU_id(this.user.getU_id());
        user.setU_sex(sex);

        String json = new Gson().toJson(user);
        String address = Constant.EDITSEX_URL;

        HttpUtil.sendPostRequest(address, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showMsg(EditUserInfoActivity.this, "性别修改失败", Toast.LENGTH_SHORT);
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
                if (msg.getCode().equals("1")) { // 性别修改成功
                    editLocalDBSex(sex);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvSex.setText(sex==0?"女":"男");
                        }
                    });
                }
            }
        });
    }
}
