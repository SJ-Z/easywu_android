package com.cose.easywu.user.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.db.User;

import org.litepal.LitePal;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditUserInfoActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mIvBack;
    private LinearLayout mLlPhoto, mLlNick, mLlSex;
    private CircleImageView mIvPhoto;
    private TextView mTvNick, mTvEmail, mTvUid, mTvSex, mTvEditPwd;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);

        initView();
        initData();
    }

    private void initData() {
        user = LitePal.findFirst(User.class);

        if (!TextUtils.isEmpty(user.getU_photo())) {
            Glide.with(this).load(user.getU_photo()).into(mIvPhoto);
        }
        mTvNick.setText(user.getU_nick());
        mTvEmail.setText(user.getU_email());
        mTvUid.setText(user.getU_id());
        mTvSex.setText(user.getU_sex()==1?"男":"女");
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_userinfo_back:
                finish();
                break;
        }
    }
}
