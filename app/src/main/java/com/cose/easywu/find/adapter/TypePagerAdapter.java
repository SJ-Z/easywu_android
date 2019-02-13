package com.cose.easywu.find.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class TypePagerAdapter extends FragmentPagerAdapter {

    public static int TYPE_FINDPEOPLE = 0;
    public static int TYPE_FINDGOODS = 1;

    private int currnetType;

    private ArrayList<Fragment> fragments;

    public TypePagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, int type) {
        super(fm);
        this.fragments = fragments;
        currnetType = type;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    // 得到页面的标题
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (currnetType == TYPE_FINDPEOPLE || currnetType == TYPE_FINDGOODS) {
            if (position == 0) {
                return "失物招领";
            } else if (position == 1) {
                return "寻物启示";
            } else {
                return "";
            }
        }
        return "";
    }
}
