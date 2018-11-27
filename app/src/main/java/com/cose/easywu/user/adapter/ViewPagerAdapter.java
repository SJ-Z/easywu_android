package com.cose.easywu.user.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public static int TYPE_MYLIKE = 0;
    public static int TYPE_MYRELEASE = 1;

    private int currnetType;

    private ArrayList<Fragment> fragments;

    public ViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, int type) {
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
        return fragments.size();
    }

    // 得到页面的标题
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (currnetType == TYPE_MYLIKE || currnetType == TYPE_MYRELEASE) {
            if (position == 0) {
                return "宝贝";
            } else if (position == 1) {
                return "寻找失物";
            } else if (position == 2) {
                return "寻找失主";
            } else {
                return "";
            }
        }
        return "";
    }
}
