package com.cose.easywu.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cose.easywu.R;
import com.cose.easywu.base.ActivityCollector;
import com.cose.easywu.base.BaseFragment;
import com.cose.easywu.cart.fragment.CartFragment;
import com.cose.easywu.home.fragment.HomeFragment;
import com.cose.easywu.message.fragment.MessageFragment;
import com.cose.easywu.release.fragment.ReleaseFragment;
import com.cose.easywu.user.fragment.UserFragment;
import com.cose.easywu.utils.HandleBackUtil;
import com.cose.easywu.utils.ToastUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity {

    FrameLayout frameLayout;
    RadioGroup rgMain;

    private ArrayList<BaseFragment> fragments;
    private int position = 0;
    // 上次显示的Fragment
    private Fragment tempFragment;

    // 是否退出程序的标志位
    private boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCollector.addActivity(this);
        // 创建数据库
        LitePal.getDatabase();

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
                        hideFragmentChoose();
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
                    ft.add(R.id.frameLayout, to, String.valueOf(position)).commit();
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

    // 隐藏Frgament切换选择条
    private void hideFragmentChoose() {
        rgMain.setVisibility(View.GONE);
    }

    // 显示Frgament切换选择条，此方法供其他类调用
    public void showFragmentChoose() {
        rgMain.setVisibility(View.VISIBLE);
    }

    // 显示首页，此方法供其他类调用
    public void showHomeFragment() {
        switchFragment(tempFragment, fragments.get(0));
        rgMain.check(R.id.rb_home);
        ((HomeFragment) fragments.get(0)).scrollToTop();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment f = getSupportFragmentManager().findFragmentByTag(String.valueOf(position));
        /*然后在碎片中调用重写的onActivityResult方法*/
        if (f != null) {
            f.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (!HandleBackUtil.handleBackPress(this)) {
            super.onBackPressed();
        }
    }

    // 重写返回键的监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if (tempFragment instanceof ReleaseFragment) {
                return super.onKeyDown(keyCode, event);
            } else {
                exitByDoubleClick();
                return true; // 表示返回键已处理完毕
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    // 双击返回键退出的处理
    private void exitByDoubleClick() {
        Timer tExit = null;
        if (!isExit) {
            isExit = true;
            ToastUtil.showMsg(MainActivity.this,"再次点击回到桌面",Toast.LENGTH_SHORT);
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;//取消退出
                }
            },2000);// 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            // 仿返回键退出界面,但不销毁，程序仍在后台运行
            moveTaskToBack(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
