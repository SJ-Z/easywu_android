package com.cose.easywu.user.fragment;

import android.view.View;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseFragment;

public class MyLikeFindPeopleFragment extends BaseFragment {

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_mylikefindpeople, null);

        initListener();
        return view;
    }

    private void initListener() {

    }

}
