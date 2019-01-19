package com.cose.easywu.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.cose.easywu.R;
import com.cose.easywu.db.HXUserInfo;
import com.cose.easywu.db.ReleaseGoods;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.gson.msg.HXMsg;
import com.cose.easywu.home.activity.GoodsInfoActivity;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class MyApplication extends Application {

    private static Context context;
    // 记录环信SDK是否已经初始化
    private boolean isInit = false;
    private EMMessageListener messageListener;
    private LocalBroadcastManager localBroadcastManager;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(context);
        // 初始化OkhttpUtils
        initOkhttpClient();
        // 初始化环信SDK
        initEasemob();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    private void initEasemob() {
        // 获取当前进程 id 并取得进程名
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        /**
         * 如果app启用了远程的service，此application:onCreate会被调用2次
         * 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
         * 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
         */
        if (processAppName == null || !processAppName.equalsIgnoreCase(context.getPackageName())) {
            // 则此application的onCreate 是被service 调用的，直接返回
            return;
        }
        if (isInit) {
            return;
        }
        /**
         * SDK初始化的一些配置
         * 关于 EMOptions 可以参考官方的 API 文档
         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1chat_1_1_e_m_options.html
         */
        EMOptions options = new EMOptions();
        // 设置Appkey，如果配置文件已经配置，这里可以不用设置
        // options.setAppKey("guaju");
        // 设置自动登录
        options.setAutoLogin(true);
        // 设置是否需要发送已读回执
        options.setRequireAck(true);
        // 设置是否需要发送回执，TODO 这个暂时有bug，上层收不到发送回执
        options.setRequireDeliveryAck(true);
        // 设置是否需要服务器收到消息确认
//        options.setRequireServerAck(true);
        options.getRequireDeliveryAck();
        // 收到好友申请是否自动同意，如果是自动同意就不会收到好友请求的回调，因为sdk会自动处理，默认为true
        options.setAcceptInvitationAlways(false);
        // 设置是否自动接收加群邀请，如果设置了当收到群邀请会自动同意加入
        options.setAutoAcceptGroupInvitation(false);
        // 设置（主动或被动）退出群组时，是否删除群聊聊天记录
        options.setDeleteMessagesAsExitGroup(false);
        // 设置是否允许聊天室的Owner 离开并删除聊天室的会话
        options.allowChatroomOwnerLeave(true);
        // 调用初始化方法初始化sdk
        EMClient.getInstance().init(context, options);
        EaseUI.getInstance().init(this, options);

        // 设置开启debug模式
        EMClient.getInstance().setDebugMode(true);

        // 设置初始化已经完成
        isInit = true;

        // 注册消息监听
        registerMessageListener();

        EaseUI.getInstance().setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });
    }

    private EaseUser getUserInfo(String username) {
        // 创建对象
        EaseUser easeUser = new EaseUser(username);
        // 先从本地数据库中查找数据
        HXUserInfo hxUserInfo = LitePal.where("uid=?", username).findFirst(HXUserInfo.class);
        if (hxUserInfo != null) {
            easeUser.setNickname(hxUserInfo.getNick());
            easeUser.setAvatar(hxUserInfo.getPhoto());
            return easeUser;
        }
        // 本地数据库没有，就子线程联网请求一条数据
        updateUserInfo(username);
        // 暂时返回ID作为昵称
        easeUser.setNickname(username);
        easeUser.setAvatar(String.valueOf(R.drawable.nav_icon));

        return easeUser;
    }

    private void updateUserInfo(final String username) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("u_id", username);
            String json = jsonObject.toString();
            HttpUtil.sendPostRequest(Constant.HXUSER_INFO_URL, json, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("MyApplication", "请求环信信息失败:" + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (null == response.body()) {
                        return;
                    }

                    String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                    final HXMsg msg = Utility.handleHXMsgResponse(responseText);
                    if (null == msg) {
                        return;
                    }
                    if (msg.getCode().equals("1")) {
                        HXUserInfo hxUserInfo = new HXUserInfo(username, msg.getNick(), msg.getPhoto());
                        hxUserInfo.save();
                        // 发送广播
                        Intent intent = new Intent(EaseConstant.HX_USER_INFO);
                        intent.putExtra("user_nick", msg.getNick());
                        localBroadcastManager.sendBroadcast(intent);
                    } else {

                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据Pid获取当前进程的名字，一般就是当前app的包名
     *
     * @param pID 进程的id
     * @return 返回进程的名字
     */
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    private void initOkhttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

    protected void registerMessageListener() {
        messageListener = new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d("MyApplication", "onMessageReceived id : " + message.getMsgId());
                    //接收并处理扩展消息
                    String uid = "";
                    String nick = "";
                    String photo = "";
                    try {
                        uid = message.getStringAttribute("uid");
                        nick = message.getStringAttribute("nick");
                        photo = message.getStringAttribute("photo");
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        // 只要有一个取不出来，就放弃缓存这条信息携带的昵称、头像
                        continue;
                    }
                    // 缓存昵称、头像
                    HXUserInfo hxUserInfo = LitePal.where("uid=?", uid).findFirst(HXUserInfo.class);
                    if (hxUserInfo != null) {
                        hxUserInfo.setNick(nick);
                        hxUserInfo.setPhoto(photo);
                    } else {
                        hxUserInfo = new HXUserInfo(uid, nick, photo);
                    }
                    hxUserInfo.save();

                    String hxIdFrom = message.getFrom();
                    EaseUser easeUser = new EaseUser(hxIdFrom);
                    easeUser.setAvatar(Constant.BASE_PHOTO_URL + photo);
                    easeUser.setNickname(nick);
                }
            }
            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d("MyApplication", "receive command message");
                    //get message body
                    //end of red packet code
                    //获取扩展属性 此处省略
                    //maybe you need get extension of your message
                    //message.getStringAttribute("");
                }
            }
            @Override
            public void onMessageRead(List<EMMessage> messages) {
            }
            @Override
            public void onMessageDelivered(List<EMMessage> message) {
            }

            @Override
            public void onMessageRecalled(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

}
