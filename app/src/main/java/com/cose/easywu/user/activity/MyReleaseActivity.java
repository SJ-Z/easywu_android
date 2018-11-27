package com.cose.easywu.user.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.user.adapter.ViewPagerAdapter;
import com.cose.easywu.user.fragment.mylike.MyLikeFindGoodsFragment;
import com.cose.easywu.user.fragment.mylike.MyLikeFindPeopleFragment;
import com.cose.easywu.user.fragment.mylike.MyLikeGoodsFragment;
import com.cose.easywu.user.fragment.myrelease.MyReleaseFindGoodsFragment;
import com.cose.easywu.user.fragment.myrelease.MyReleaseFindPeopleFragment;
import com.cose.easywu.user.fragment.myrelease.MyReleaseGoodsFragment;

import java.util.ArrayList;

public class MyReleaseActivity extends BaseActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ArrayList<Fragment> fragments;
    private ViewPagerAdapter adapter;

    private ImageView mIvBack;
    private TextView mTvUnsold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_release);

        initView();
        initFragment();
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
    }

    private void initData() {
        // 设置ViewPager的适配器
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments, ViewPagerAdapter.TYPE_MYRELEASE);
        viewPager.setAdapter(adapter);
        // TabLayout关联ViewPager
        tabLayout.setupWithViewPager(viewPager);
        // 设置TabLayout的滑动效果
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new MyReleaseGoodsFragment());
        fragments.add(new MyReleaseFindGoodsFragment());
        fragments.add(new MyReleaseFindPeopleFragment());
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager_myrelease);
        tabLayout = findViewById(R.id.tabLayout_myrelease);
        mIvBack = findViewById(R.id.iv_myrelease_back);
        mTvUnsold = findViewById(R.id.tv_myrelease_unsold);
    }
}
