package com.cose.easywu.user.fragment.myrelease;

import android.view.View;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseFragment;

public class MyReleaseFindPeopleFragment extends BaseFragment {

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_myrelease_findpeople, null);

        initListener();
        return view;
    }

    private void initListener() {

    }

}