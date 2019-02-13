package com.cose.easywu.find.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.find.adapter.TypePagerAdapter;
import com.cose.easywu.find.fragment.FindTypeGoodsFragment;
import com.cose.easywu.find.fragment.FindTypePeopleFragment;

import java.util.ArrayList;

public class FindTypeGoodsActivity extends BaseActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ArrayList<Fragment> fragments;
    private TypePagerAdapter adapter;

    private TextView mTvBack, mTvTypeName;

    private String type_id;
    private String type_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_type_goods);initView();
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
        Intent intent = getIntent();
        type_id = intent.getStringExtra("type_id");
        type_name = intent.getStringExtra("type_name");
        mTvTypeName.setText(type_name);
        fragments = new ArrayList<>();
        FindTypeGoodsFragment findTypeGoodsFragment = new FindTypeGoodsFragment();
        FindTypePeopleFragment findTypePeopleFragment = new FindTypePeopleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type_id", type_id);
        findTypeGoodsFragment.setArguments(bundle);
        findTypePeopleFragment.setArguments(bundle);
        fragments.add(findTypeGoodsFragment);
        fragments.add(findTypePeopleFragment);
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager_type_goods);
        tabLayout = findViewById(R.id.tabLayout_type_goods);
        mTvBack = findViewById(R.id.tv_type_goods_back);
        mTvTypeName = findViewById(R.id.tv_type_goods_type_name);
    }
}
