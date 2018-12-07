package com.cose.easywu.user.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.user.adapter.ViewPagerAdapter;
import com.cose.easywu.user.fragment.myrelease.MyClosedFindGoodsFragment;
import com.cose.easywu.user.fragment.myrelease.MyClosedFindPeopleFragment;
import com.cose.easywu.user.fragment.myrelease.MyClosedGoodsFragment;

import java.util.ArrayList;

public class ClosedActivity extends BaseActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ArrayList<Fragment> fragments;
    private ViewPagerAdapter adapter;

    private ImageView mIvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closed);

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
        fragments.add(new MyClosedGoodsFragment());
        fragments.add(new MyClosedFindGoodsFragment());
        fragments.add(new MyClosedFindPeopleFragment());
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager_myrelease_closed);
        tabLayout = findViewById(R.id.tabLayout_myrelease_closed);
        mIvBack = findViewById(R.id.iv_myrelease_closed_back);
    }
}
