package com.cose.easywu.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.cose.easywu.R;
import com.cose.easywu.base.ActivityCollector;
import com.cose.easywu.base.BaseFragment;
import com.cose.easywu.cart.fragment.CartFragment;
import com.cose.easywu.home.fragment.HomeFragment;
import com.cose.easywu.message.fragment.MessageFragment;
import com.cose.easywu.release.fragment.ReleaseFragment;
import com.cose.easywu.user.fragment.UserFragment;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    FrameLayout frameLayout;
    RadioGroup rgMain;

    private ArrayList<BaseFragment> fragments;
    private int position = 0;
    // 上次显示的Fragment
    private Fragment tempFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCollector.addActivity(this);

        initView();
        // 初始化Fragment
        initFragment();
        // 设置RadioGroup的监听
        initListener();
    }

    // 设置RadioGroup的监听
    private void initListener() {
        rgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home: // 主页
                        position = 0;
                        break;
                    case R.id.rb_cart: // 分类
                        position = 1;
                        break;
                    case R.id.rb_release: // 发布
                        position = 2;
                        break;
                    case R.id.rb_message: // 发现
                        position = 3;
                        break;
                    case R.id.rb_user: // 用户中心
                        position = 4;
                        break;
                    default:
                        position = 0;
                        break;
                }
                // 根据位置取不同的Fragment
                BaseFragment to = getFragment(position);
                // 第一个参数：上次显示的Fragment，第二个参数：当前正要显示的Fragment
                switchFragment(tempFragment, to);
            }
        });

        rgMain.check(R.id.rb_home);
    }

    private void initView() {
        frameLayout = findViewById(R.id.frameLayout);
        rgMain = findViewById(R.id.rg_main);
    }

    // 解决了切换Fragment导致Fragment重新被创建的问题
    private void switchFragment(Fragment from, Fragment to) {
        if (from != to) {
            tempFragment = to;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // 判断to是否被添加过
            if (!to.isAdded()) { // to没有被添加
                //from隐藏
                if (from != null) {
                    ft.hide(from);
                }
                //添加to
                if (to != null) {
                    ft.add(R.id.frameLayout, to).commit();
                }
            } else {  // to已经被添加过
                //from隐藏
                if (from != null) {
                    ft.hide(from);
                }
                //显示to
                if (to != null) {
                    ft.show(to).commit();
                }
            }
        }
    }

    // 根据位置得到对应的Fragment
    private BaseFragment getFragment(int position) {
        if (fragments != null && fragments.size() > 0) {
            return fragments.get(position);
        }
        return null;
    }

    // 初始化Fragment
    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new CartFragment());
        fragments.add(new ReleaseFragment());
        fragments.add(new MessageFragment());
        fragments.add(new UserFragment());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
