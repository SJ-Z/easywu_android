package com.cose.easywu.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cose.easywu.R;
import com.cose.easywu.base.ActivityCollector;
import com.cose.easywu.db.Notification;
import com.cose.easywu.find.fragment.FindFragment;
import com.cose.easywu.home.activity.GoodsInfoActivity;
import com.cose.easywu.home.fragment.HomeFragment;
import com.cose.easywu.message.fragment.MessageFragment;
import com.cose.easywu.release.activity.ReleaseActivity;
import com.cose.easywu.user.activity.MySellActivity;
import com.cose.easywu.user.fragment.UserFragment;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HandleBackUtil;
import com.cose.easywu.utils.NotificationHelper;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.widget.PublishDialog;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.model.GoodsMessageHelper;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.service.JPushMessageReceiver;

public class MainActivity extends FragmentActivity {

    private RadioGroup rgMain;
    private PublishDialog publishDialog;
    private TextView msgNum;

    private BroadcastReceiver receiver_new_msg;
    private LocalBroadcastManager localBroadcastManager;

    private ArrayList<Fragment> fragments;
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
        initData();
        // 初始化Fragment
        initFragment();
        // 设置RadioGroup的监听
        initListener();

        // 判断是否响应通知，跳转到商品界面
        checkTurn2GoodsInfoActivity();
    }

    private void checkTurn2GoodsInfoActivity() {
        Intent originIntent = getIntent();
        if (null != originIntent.getType() && originIntent.getType().equals("1")) {
            return;
        } else {
            if (originIntent.getBooleanExtra(GoodsMessageHelper.CHATTYPE, false)) {
                Intent intent = new Intent(MainActivity.this, GoodsInfoActivity.class);
                intent.putExtra(GoodsMessageHelper.CHATTYPE, true); // 让GoodsInfoActivity识别的标志位
                intent.putExtra(GoodsMessageHelper.GOODS_ID, originIntent.getStringExtra(GoodsMessageHelper.GOODS_ID));
                startActivity(intent);
                originIntent.setType("1");
            } else if (originIntent.getBooleanExtra(GoodsMessageHelper.NewGoodsOrderType, false)) {
                Intent intent = new Intent(MainActivity.this, MySellActivity.class);
                startActivity(intent);
                originIntent.setType("1");
            } else if (originIntent.getBooleanExtra(GoodsMessageHelper.ConfirmGoodsOrderType, false)) {
                Intent intent = new Intent(MainActivity.this, GoodsInfoActivity.class);
                intent.putExtra(GoodsMessageHelper.ConfirmGoodsOrderType, true); // 让GoodsInfoActivity识别的标志位
                intent.putExtra(GoodsMessageHelper.GOODS_ID, originIntent.getStringExtra(GoodsMessageHelper.GOODS_ID));
                startActivity(intent);
                originIntent.setType("1");
            } else if (originIntent.getBooleanExtra(GoodsMessageHelper.RefuseGoodsOrderType, false)) {
                Intent intent = new Intent(MainActivity.this, GoodsInfoActivity.class);
                intent.putExtra(GoodsMessageHelper.RefuseGoodsOrderType, true); // 让GoodsInfoActivity识别的标志位
                intent.putExtra(GoodsMessageHelper.GOODS_ID, originIntent.getStringExtra(GoodsMessageHelper.GOODS_ID));
                startActivity(intent);
                originIntent.setType("1");
            }
        }

    }

    // 设置RadioGroup的监听
    private void initListener() {
        rgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home: // 跳蚤市场
                        position = 0;
                        break;
                    case R.id.rb_find: // 失物招领
                        position = 1;
                        break;
                    case R.id.rb_release: // 发布
                        rgMain.check(getCurrentFragmentId());
                        showPublishDialog();
                        break;
                    case R.id.rb_message: // 消息
                        position = 2;
                        break;
                    case R.id.rb_user: // 用户中心
                        position = 3;
                        break;
                    default:
                        position = 0;
                        break;
                }
                // 根据位置取不同的Fragment
                Fragment to = getFragment(position);
                // 第一个参数：上次显示的Fragment，第二个参数：当前正要显示的Fragment
                switchFragment(tempFragment, to);
            }
        });

        if (getIntent() != null && getIntent().getBooleanExtra(GoodsMessageHelper.CHATTYPE, false)) {
            rgMain.check(R.id.rb_message);
        } else {
            rgMain.check(R.id.rb_home);
        }
    }

    private int getCurrentFragmentId() {
        if (position == 0) {
            return R.id.rb_home;
        } else if (position == 1) {
            return R.id.rb_find;
        } else if (position == 2) {
            return R.id.rb_message;
        } else if (position == 3) {
            return R.id.rb_user;
        }

        return R.id.rb_home;
    }

    private void showPublishDialog() {
        if (publishDialog == null) {
            publishDialog = new PublishDialog(MainActivity.this);
            publishDialog.setFabuClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, ReleaseActivity.class));
                    publishDialog.outDia();
                }
            });
            publishDialog.setHuishouClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "寻找失主", Toast.LENGTH_SHORT).show();
                }
            });
            publishDialog.setPingguClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "寻找失物", Toast.LENGTH_SHORT).show();
                }
            });
        }
        publishDialog.show();
    }

    // 未读消息数量 = 未读用户消息 + 未读系统消息
    private void setUnreadMsgNum() {
        int userMsgCount = EMClient.getInstance().chatManager().getUnreadMessageCount();
        int systemMsgCount = LitePal.where("state=?", String.valueOf(
                NotificationHelper.STATE_RECEIVE)).count(Notification.class);
        int totalCount = userMsgCount + systemMsgCount;
        if (totalCount > 0) {
            if (totalCount < 100) {
                msgNum.setText(String.valueOf(totalCount));
            } else {
                msgNum.setText("99+");
            }
            msgNum.setVisibility(View.VISIBLE);
        } else {
            msgNum.setVisibility(View.GONE);
        }
        if (((MessageFragment) fragments.get(2)).isInit) {
            ((MessageFragment) fragments.get(2)).initContent();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUnreadMsgNum();
        checkTurn2GoodsInfoActivity();
    }

    private void initData() {
        // 初始化极光推送的别名
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        JPushInterface.setAlias(MainActivity.this, 0, pref.getString("u_id", ""));
        // 注册广播接收器
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter_new_msg = new IntentFilter();
        intentFilter_new_msg.addAction(Constant.RECEIVE_NEW_MESSAGE);
        receiver_new_msg = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setUnreadMsgNum(); // 刷新未读消息数量
            }
        };
        localBroadcastManager.registerReceiver(receiver_new_msg, intentFilter_new_msg);
    }

    private void initView() {
        rgMain = findViewById(R.id.rg_main);
        msgNum = findViewById(R.id.msg_num);
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
    private Fragment getFragment(int position) {
        if (fragments != null && fragments.size() > 0) {
            return fragments.get(position);
        }
        return null;
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
        fragments.add(new FindFragment());
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByDoubleClick();
            return true; // 表示返回键已处理完毕
        }
        return super.onKeyDown(keyCode, event);
    }

    // 双击返回键退出的处理
    private void exitByDoubleClick() {
        Timer tExit = null;
        if (!isExit) {
            isExit = true;
            ToastUtil.showMsg(MainActivity.this, "再次点击回到桌面", Toast.LENGTH_SHORT);
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;//取消退出
                }
            }, 2000);// 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            // 仿返回键退出界面,但不销毁，程序仍在后台运行
            moveTaskToBack(false);
        }
    }

    //此方法在onResume之前执行
    @Override
    protected void onNewIntent(Intent intent) {
        //每次重新到前台就主动更新intent并保存，之后就能获取到最新的intent
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver_new_msg != null) {
            localBroadcastManager.unregisterReceiver(receiver_new_msg);
            receiver_new_msg = null;
        }
        ActivityCollector.removeActivity(this);
    }
}
