package com.cose.easywu.message.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.db.Notification;
import com.cose.easywu.message.adapter.SystemMsgAdapter;
import com.cose.easywu.utils.NotificationHelper;

import org.litepal.LitePal;

import java.util.List;

public class SystemMsgActivity extends BaseActivity {

    private TextView mTvBack;
    private RecyclerView mRecyclerView;
    private List<Notification> notificationList;
    private SystemMsgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_msg);

        initView();
        initData();
    }

    private void initData() {
        notificationList = LitePal.order("time desc").find(Notification.class);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new SystemMsgAdapter(notificationList);
        mRecyclerView.setAdapter(adapter);
        // 将所有未读系统消息设置为已读
        List<Notification> notificationList = LitePal.where("state=?", String.valueOf(
                NotificationHelper.STATE_RECEIVE)).find(Notification.class);
        for (Notification notification : notificationList) {
            notification.setState(NotificationHelper.STATE_READ);
            notification.save();
        }
    }

    private void initView() {
        mTvBack = findViewById(R.id.tv_system_msg_back);
        mRecyclerView = findViewById(R.id.recyclerview_system_msg);
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
