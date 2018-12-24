package com.cose.easywu.message.fragment;

import android.preference.PreferenceManager;
import android.view.View;

import com.cose.easywu.db.HXUserInfo;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;

import org.litepal.LitePal;

public class MyChatFragment extends EaseChatFragment implements EaseChatFragment.EaseChatFragmentHelper {

    @Override
    protected void setUpView() {
        // 设置聊天界面的监听,需要在父类 onActivityCreated 之前设置监听
        setChatFragmentHelper(this);
        super.setUpView();
    }

    // 在这个方法设置拓展消息
    @Override
    public void onSetMessageAttributes(EMMessage message) {
        // 先拿到当前用户的信息
        String uid = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("u_id", "");
        HXUserInfo hxUserInfo = LitePal.where("uid=?", uid).findFirst(HXUserInfo.class);
        if (hxUserInfo != null) {
            // 携带进消息里面去
            message.setAttribute("uid", uid);
            message.setAttribute("nick", hxUserInfo.getNick());
            message.setAttribute("photo", hxUserInfo.getPhoto());
        }
    }

    @Override
    public void onEnterToChatDetails() {

    }

    @Override
    public void onAvatarClick(String username) {

    }

    @Override
    public void onAvatarLongClick(String username) {

    }

    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        //消息框点击事件，这里不做覆盖，如需覆盖，return true
        return false;
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {

    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        return false;
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return null;
    }
}
