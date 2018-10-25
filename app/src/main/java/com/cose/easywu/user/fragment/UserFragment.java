package com.cose.easywu.user.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.cose.easywu.R;
import com.cose.easywu.app.LoginActivity;
import com.cose.easywu.base.ActivityCollector;
import com.cose.easywu.base.BaseFragment;
import com.cose.easywu.base.MyApplication;

public class UserFragment extends BaseFragment {

    private Button mBtnExit;
    private SharedPreferences.Editor editor;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_user, null);
        mBtnExit = view.findViewById(R.id.btn_main_exit);

        // 设置点击事件
        initListener();
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
    }

    private void initListener() {
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
    }
}
