package com.cose.easywu.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.cose.easywu.app.MainActivity;
import com.cose.easywu.utils.HandleBackUtil;
import com.cose.easywu.utils.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

public class BaseActivity extends AppCompatActivity {

    private boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void onBackPressed() {
        if (!HandleBackUtil.handleBackPress(this)) {
            super.onBackPressed();
        }
    }

//    // 重写返回键的监听
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            if (ActivityCollector.getActivitiesLength() <= 1) {
//                exitByDoubleClick();
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    private void exitByDoubleClick() {
//        Timer tExit = null;
//        if (!isExit) {
//            isExit = true;
//            ToastUtil.showMsg(BaseActivity.this,"再次点击回到桌面",Toast.LENGTH_SHORT);
//            tExit = new Timer();
//            tExit.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    isExit = false;//取消退出
//                }
//            },2000);// 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
//        }
//    }

}
