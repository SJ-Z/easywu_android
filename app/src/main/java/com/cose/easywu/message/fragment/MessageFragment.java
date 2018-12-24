package com.cose.easywu.message.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cose.easywu.message.activity.ChatActivity;
import com.cose.easywu.utils.HandleBackInterface;
import com.cose.easywu.utils.HandleBackUtil;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import java.util.List;

public class MessageFragment extends EaseConversationListFragment implements HandleBackInterface {
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
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
    public boolean onBackPressed() {
        return HandleBackUtil.handleBackPress(this);
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
