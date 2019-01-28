package com.cose.easywu.message.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.cose.easywu.db.HXUserInfo;
import com.cose.easywu.db.Notification;
import com.cose.easywu.message.activity.ChatActivity;
import com.cose.easywu.message.activity.SystemMsgActivity;
import com.cose.easywu.utils.DateUtil;
import com.cose.easywu.utils.HandleBackInterface;
import com.cose.easywu.utils.HandleBackUtil;
import com.cose.easywu.utils.NotificationHelper;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import org.litepal.LitePal;

import java.util.List;

public class MessageFragment extends EaseConversationListFragment implements HandleBackInterface {
    private Context mContext;
    public boolean isInit = false; // 初始化的标志位

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        if (getActivity() != null) { // 设置默认软键盘不弹出
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        // 修改初始化的标志位
        isInit = true;
    }

    @Override
    protected void initView() {
        super.initView();

        // 设置跳转到会话详情页面的点击事件
        setConversationListItemClickListener(new EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                // 传递参数，会话id即环信id
                intent.putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId());
                startActivity(intent);
            }
        });

        // 解决低版本列表闪动问题，先清空列表
        conversationList.clear();
        // 监听会话消息
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
    }

    @Override
    protected void initSystemMessageInterface() {
        systemMessageInterface = new SystemMessageInterface() {
            @Override
            public void onClick() {
                startActivity(new Intent(getContext(), SystemMsgActivity.class));
            }

            @Override
            public void setUnreadSystemMsgNum() {
                int count = LitePal.where("state=?",
                        String.valueOf(NotificationHelper.STATE_RECEIVE)).count(Notification.class);
                if (count > 0) {
                    tv_system_msg_num.setText(String.valueOf(count));
                    tv_system_msg_num.setVisibility(View.VISIBLE);
                } else {
                    tv_system_msg_num.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void initContent() {
                Notification notification = LitePal.order("time desc").findFirst(Notification.class);
                if (notification != null) {
                    tv_system_msg_time.setText(DateUtil.getShowTime(notification.getTime()));
                    tv_system_msg_content.setText(notification.getContent());
                } else {
                    tv_system_msg_content.setText("暂时没有系统消息");
                }
            }
        };
    }

    public void initContent() {
        Notification notification = LitePal.order("time desc").findFirst(Notification.class);
        int unreadCount = LitePal.where("state=?", String.valueOf(NotificationHelper.STATE_RECEIVE))
                .count(Notification.class);
        if (notification != null) {
            tv_system_msg_time.setText(DateUtil.getShowTime(notification.getTime()));
            tv_system_msg_content.setText(notification.getContent());
            if (unreadCount > 0) {
                tv_system_msg_num.setText(String.valueOf(unreadCount));
                tv_system_msg_num.setVisibility(View.VISIBLE);
            }
        } else {
            tv_system_msg_content.setText("暂时没有系统消息");
        }
    }

    @Override
    public boolean onBackPressed() {
        return HandleBackUtil.handleBackPress(this);
    }

    @Override
    protected CharSequence searchUser(CharSequence s) {
        HXUserInfo hxUserInfo = LitePal.where("nick LIKE ?", s.toString() + "%").findFirst(HXUserInfo.class);
        if (hxUserInfo != null) {
            return hxUserInfo.getUid();
        }
        return "#";
    }

    private EMMessageListener emMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            // 设置数据
            EaseUI.getInstance().getNotifier().notify(list);
            // 刷新页面
            refresh();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageRead(List<EMMessage> list) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {

        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };
}
