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
import com.cose.easywu.user.fragment.MyLikeFindGoodsFragment;
import com.cose.easywu.user.fragment.MyLikeFindPeopleFragment;
import com.cose.easywu.user.fragment.MyLikeGoodsFragment;

import java.util.ArrayList;

public class MyLikeActivity extends BaseActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ArrayList<Fragment> fragments;
    private ViewPagerAdapter adapter;

    private ImageView mIvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_like);

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
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        // TabLayout关联ViewPager
        tabLayout.setupWithViewPager(viewPager);
        // 设置TabLayout的滑动效果
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new MyLikeGoodsFragment());
        fragments.add(new MyLikeFindGoodsFragment());
        fragments.add(new MyLikeFindPeopleFragment());
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager_mylike);
        tabLayout = findViewById(R.id.tabLayout_mylike);
        mIvBack = findViewById(R.id.iv_mylike_back);
    }
}
