package com.cose.easywu.user.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cose.easywu.R;
import com.cose.easywu.app.LoginActivity;
import com.cose.easywu.app.MainActivity;
import com.cose.easywu.base.ActivityCollector;
import com.cose.easywu.base.BaseFragment;
import com.cose.easywu.base.MyApplication;
import com.cose.easywu.gson.User;
import com.cose.easywu.gson.msg.LoginMsg;
import com.cose.easywu.gson.msg.PersonMsg;
import com.cose.easywu.user.activity.EditUserInfoActivity;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.Utility;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.LitePal;

import java.io.IOException;
import java.net.URLDecoder;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserFragment extends BaseFragment {

    private TextView mTvNick, mTvGain, mTvMyreleaseCount, mTvMysellCount, mTvMybuyCount, mTvMylikeCount;
    private CircleImageView mIvPhoto;
    private ImageView mIvSex;
    private LinearLayout mLlMyrelease, mLlMysell, mLlMybuy, mLlMylike, mLlSettinng, mLlClear;
    private Button mBtnExit;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    public void onResume() {
        super.onResume();
        // 更新名称、性别、头像
        com.cose.easywu.db.User user = LitePal.findFirst(com.cose.easywu.db.User.class);
        mTvNick.setText(user.getU_nick());
        mIvSex.setImageResource(user.getU_sex()==0 ? R.drawable.ic_female : R.drawable.ic_male);
        if (!TextUtils.isEmpty(user.getU_photo())) {
            Glide.with(getActivity()).load(user.getU_photo()).into(mIvPhoto);
        }
    }

    @Override
    public void initData() {
        super.initData();
        pref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        editor = pref.edit();
        // 联网请求个人中心的数据
        getDataFromServer();
    }

    private void getDataFromServer() {
        String json = pref.getString("u_id", "");
        String address = Constant.PERSONAL_CENTER_URL;
        HttpUtil.sendPostRequest(address, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null == response.body()) {
                    return;
                }

                String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                // 解析数据
                processData(responseText);
            }
        });
    }

    // 解析数据
    private void processData(String response) {
        final PersonMsg personMsg = Utility.handlePersonMsgResponse(response);
        if (null == personMsg) {
            return;
        }

        // 更新界面
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                User user = personMsg.getUser();
                mTvNick.setText(user.getU_nick());
                mTvGain.setText(String.valueOf(user.getU_gain()));
                String photo = user.getU_photo();
                if (null != photo) {
                    Glide.with(getActivity()).load(photo).into(mIvPhoto);
                }
                boolean male = user.getU_sex() == 1;
                if (male) {
                    Glide.with(getActivity()).load(R.drawable.ic_male).into(mIvSex);
                } else {
                    Glide.with(getActivity()).load(R.drawable.ic_female).into(mIvSex);
                }
            }
        });

        // 保存用户数据到本地数据库
        saveToDatabase(personMsg);
    }

    // 保存用户数据到本地数据库
    private void saveToDatabase(PersonMsg personMsg) {
        User user = personMsg.getUser();
        com.cose.easywu.db.User dbUser = new com.cose.easywu.db.User(user.getU_id(), user.getU_email(),
                user.getU_nick(), user.getU_photo(), user.getU_sex(), user.getU_gain(), user.getU_state());
        dbUser.save();
    }

    private void initListener() {
        // 退出登录
        mBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.remove("u_id");
                editor.putBoolean("autoLogin", false);
                editor.apply();
                ActivityCollector.finishAll();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        // 个人资料设置
        mLlSettinng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditUserInfoActivity.class));
            }
        });
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_user, null);
        mTvNick = view.findViewById(R.id.tv_user_nick);
        mTvGain = view.findViewById(R.id.tv_user_gain);
        mTvMyreleaseCount = view.findViewById(R.id.tv_user_myrelease_count);
        mTvMysellCount = view.findViewById(R.id.tv_user_mysell_count);
        mTvMybuyCount = view.findViewById(R.id.tv_user_mybuy_count);
        mTvMylikeCount = view.findViewById(R.id.tv_user_mylike_count);
        mIvSex = view.findViewById(R.id.iv_user_sex);
        mIvPhoto = view.findViewById(R.id.iv_user_photo);
        mLlMyrelease = view.findViewById(R.id.ll_user_myrelease);
        mLlMysell = view.findViewById(R.id.ll_user_mysell);
        mLlMybuy = view.findViewById(R.id.ll_user_mybuy);
        mLlMylike = view.findViewById(R.id.ll_user_mylike);
        mLlSettinng = view.findViewById(R.id.ll_user_setting);
        mLlClear = view.findViewById(R.id.ll_user_clear);
        mBtnExit = view.findViewById(R.id.btn_main_exit);

        // 设置点击事件
        initListener();
        return view;
    }
}
