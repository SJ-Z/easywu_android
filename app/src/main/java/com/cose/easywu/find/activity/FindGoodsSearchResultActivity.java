package com.cose.easywu.find.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.find.adapter.TypePagerAdapter;
import com.cose.easywu.find.fragment.SearchFindGoodsFragment;
import com.cose.easywu.find.fragment.SearchFindPeopleFragment;

import java.util.ArrayList;

public class FindGoodsSearchResultActivity extends BaseActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ArrayList<Fragment> fragments;
    private TypePagerAdapter adapter;
    private TextView mTvBack;
    private String key; // 查询的关键字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_goods_search_result);
        initView();
        initFragment();
        initData();
        initListener();
    }

    private void initListener() {
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        // 设置ViewPager的适配器
        adapter = new TypePagerAdapter(getSupportFragmentManager(), fragments, TypePagerAdapter.TYPE_FINDPEOPLE);
        viewPager.setAdapter(adapter);
        // TabLayout关联ViewPager
        tabLayout.setupWithViewPager(viewPager);
        // 设置TabLayout的滑动效果
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    private void initFragment() {
        key = getIntent().getStringExtra("key");
        fragments = new ArrayList<>();
        SearchFindGoodsFragment searchFindGoodsFragment = new SearchFindGoodsFragment();
        SearchFindPeopleFragment searchFindPeopleFragment = new SearchFindPeopleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("key", key);
        searchFindGoodsFragment.setArguments(bundle);
        searchFindPeopleFragment.setArguments(bundle);
        fragments.add(searchFindGoodsFragment);
        fragments.add(searchFindPeopleFragment);
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager_search_goods);
        tabLayout = findViewById(R.id.tabLayout_search_goods);
        mTvBack = findViewById(R.id.tv_search_goods_back);
    }
}
