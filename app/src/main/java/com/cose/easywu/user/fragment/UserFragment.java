package com.cose.easywu.user.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.app.LoginActivity;
import com.cose.easywu.base.ActivityCollector;
import com.cose.easywu.base.BaseFragment;
import com.cose.easywu.base.MyApplication;
import com.cose.easywu.db.LikeGoods;
import com.cose.easywu.db.ReleaseGoods;
import com.cose.easywu.db.SellGoods;
import com.cose.easywu.gson.User;
import com.cose.easywu.gson.msg.PersonMsg;
import com.cose.easywu.user.activity.EditUserInfoActivity;
import com.cose.easywu.user.activity.MyLikeActivity;
import com.cose.easywu.user.activity.MyReleaseActivity;
import com.cose.easywu.user.activity.MySellActivity;
import com.cose.easywu.utils.CacheUtils;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ImageUtils;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;
import com.cose.easywu.widget.MessageDialog;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.litepal.LitePal;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserFragment extends BaseFragment {

    private TextView mTvNick, mTvGain, mTvMyreleaseCount, mTvMysellCount, mTvMybuyCount, mTvMylikeCount, mTvCacheSize;
    private static CircleImageView mIvPhoto;
    private ImageView mIvSex;
    private LinearLayout mLlMyrelease, mLlMysell, mLlMybuy, mLlMylike, mLlSettinng, mLlClear;
    private Button mBtnExit;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private com.cose.easywu.db.User dbUser;

    @Override
    public void onResume() {
        super.onResume();
        // 更新名称、性别、头像
        String u_id = pref.getString("u_id", "");
        com.cose.easywu.db.User dbUser = LitePal.where("u_id=?", u_id).findFirst(com.cose.easywu.db.User.class);
        if (dbUser != null) {
            mTvNick.setText(dbUser.getU_nick());
            mIvSex.setImageResource(dbUser.getU_sex() == 0 ? R.drawable.ic_female : R.drawable.ic_male);
            if (!TextUtils.isEmpty(dbUser.getU_photo())) {
                Glide.with(mContext).load(Constant.BASE_PHOTO_URL + dbUser.getU_photo())
                        .apply(new RequestOptions().placeholder(R.drawable.nav_icon))
                        .into(mIvPhoto);
            }
            Bitmap photo = ImageUtils.getPhotoFromStorage(dbUser.getU_id());
            Glide.with(this).load(photo)
                    .apply(new RequestOptions().placeholder(R.drawable.nav_icon))
                    .into(mIvPhoto);
        }
        // 更新收藏的商品数量
        int likeGoodsCount = LitePal.findAll(LikeGoods.class).size();
        mTvMylikeCount.setText(String.valueOf(likeGoodsCount));
        // 更新“我发布的”和“我卖出的”商品数量
        int myReleaseCount = LitePal.where("g_state=?", "0").find(ReleaseGoods.class).size();
        List<SellGoods> sellGoodsList = LitePal.findAll(SellGoods.class);
        mTvMyreleaseCount.setText(String.valueOf(myReleaseCount));
        mTvMysellCount.setText(String.valueOf(sellGoodsList.size()));
        // 更新“在简物赚了xx元”的价格
        double myGainMoney = 0.0;
        for (SellGoods sellGoods : sellGoodsList) {
            myGainMoney += sellGoods.getG_price();
        }
        mTvGain.setText(String.valueOf(myGainMoney));
        // 设置缓存数据
        mTvCacheSize.setText(CacheUtils.getTotalCacheSize(MyApplication.getContext()));
    }

    @Override
    public void initData() {
        super.initData();
        pref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        editor = pref.edit();
        final String u_id = pref.getString("u_id", "");
        dbUser = LitePal.where("u_id=?", u_id).findFirst(com.cose.easywu.db.User.class);
        if (dbUser == null) {
            dbUser = new com.cose.easywu.db.User();
        }
        // 联网请求个人中心的数据
        getDataFromServer(u_id);
    }

    private void getDataFromServer(String u_id) {
        String address = Constant.PERSONAL_CENTER_URL;
        HttpUtil.sendPostRequest(address, u_id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null == response.body()) {
                    return;
                }

                String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                // 解析数据
                processData(responseText);
            }
        });
    }

    // 解析数据
    private void processData(String response) {
        final PersonMsg personMsg = Utility.handlePersonMsgResponse(response);
        if (null == personMsg) {
            return;
        }

        final User user = personMsg.getUser();

        // 更新界面
        if (getActivity() == null) {
            return;
        }

        final Bitmap bitmap = ImageUtils.getPhotoFromStorage(user.getU_id());
        if (bitmap != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("加载用户头像", "本地加载头像");
                    Glide.with(getActivity()).load(bitmap)
                            .apply(new RequestOptions().placeholder(R.drawable.nav_icon))
                            .into(mIvPhoto);
                }
            });
        } else {
            // 从服务器请求头像数据
            asyncGet(Constant.BASE_PHOTO_URL + user.getU_photo());
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvNick.setText(user.getU_nick());
                mTvGain.setText(String.valueOf(user.getU_gain()));
                boolean male = user.getU_sex() == 1;
                if (male) {
                    Glide.with(getActivity()).load(R.drawable.ic_male).into(mIvSex);
                } else {
                    Glide.with(getActivity()).load(R.drawable.ic_female).into(mIvSex);
                }
            }
        });

        // 保存用户数据到本地数据库
        saveToDatabase(personMsg);
    }

    private void asyncGet(String imgUrl) {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().get()
                .url(imgUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        byte[] bytes = response.body().bytes();
                        final Bitmap bitmap = ImageUtils.getBitmapFromByte(bytes, 70, 70);
                        ImageUtils.savePhotoToStorage(bitmap, pref.getString("u_id", ""));
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(getActivity()).load(bitmap)
                                            .apply(new RequestOptions().placeholder(R.drawable.nav_icon))
                                            .into(mIvPhoto);
                                }
                            });
                            Log.i("加载用户头像", "从服务器加载头像");
                        }
                    }
                }
            }
        });
    }

    // 保存用户数据到本地数据库
    private void saveToDatabase(PersonMsg personMsg) {
        User user = personMsg.getUser();
        dbUser.setU_id(user.getU_id());
        dbUser.setU_email(user.getU_email());
        dbUser.setU_nick(user.getU_nick());
        dbUser.setU_photo(user.getU_photo());
        dbUser.setU_sex(user.getU_sex());
        dbUser.setU_gain(user.getU_gain());
        dbUser.setU_state(user.getU_state());

        dbUser.save();
    }

    private void initListener() {
        // 退出登录
        mBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.remove("u_id");
                editor.putBoolean("autoLogin", false);
                editor.apply();

                // 退出环信
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 登录环信服务器退出登录
                        EMClient.getInstance().logout(false, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 回到登录页面
                                            ActivityCollector.finishAll();
                                            startActivity(new Intent(mContext, LoginActivity.class));
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError(int i, final String s) {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(), "退出失败：" + s, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onProgress(int i, String s) {

                            }
                        });
                    }
                }).start();
            }
        });
        // 个人资料设置
        mLlSettinng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, EditUserInfoActivity.class));
            }
        });
        // 清除缓存
        mLlClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDialog messageDialog = new MessageDialog(mContext, R.style.MessageDialog);
                messageDialog.setTitle("提示").setContent("所有缓存数据将被清空，是否继续？")
                        .setCancel("取消", new MessageDialog.IOnCancelListener() {
                            @Override
                            public void onCancel(MessageDialog dialog) {

                            }
                        }).setConfirm("确认", new MessageDialog.IOnConfirmListener() {
                    @Override
                    public void onConfirm(MessageDialog dialog) {
                        if (CacheUtils.clearAllCache(MyApplication.getContext())) {
                            mTvCacheSize.setText(CacheUtils.getTotalCacheSize(MyApplication.getContext()));
                            ToastUtil.showMsgOnCenter(mContext, "清除缓存成功", Toast.LENGTH_SHORT);
                        } else {
                            ToastUtil.showMsgOnCenter(mContext, "清除缓存失败", Toast.LENGTH_SHORT);
                        }
                    }
                }).show();
            }
        });
        // 我收藏的
        mLlMylike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, MyLikeActivity.class));
            }
        });
        // 我发布的
        mLlMyrelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, MyReleaseActivity.class));
            }
        });
        // 我卖出的
        mLlMysell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, MySellActivity.class));
            }
        });
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_user, null);
        mTvNick = view.findViewById(R.id.tv_user_nick);
        mTvGain = view.findViewById(R.id.tv_user_gain);
        mTvMyreleaseCount = view.findViewById(R.id.tv_user_myrelease_count);
        mTvMysellCount = view.findViewById(R.id.tv_user_mysell_count);
        mTvMybuyCount = view.findViewById(R.id.tv_user_mybuy_count);
        mTvMylikeCount = view.findViewById(R.id.tv_user_mylike_count);
        mTvCacheSize = view.findViewById(R.id.tv_user_cacheSize);
        mIvSex = view.findViewById(R.id.iv_user_sex);
        mIvPhoto = view.findViewById(R.id.iv_user_photo);
        mLlMyrelease = view.findViewById(R.id.ll_user_myrelease);
        mLlMysell = view.findViewById(R.id.ll_user_mysell);
        mLlMybuy = view.findViewById(R.id.ll_user_mybuy);
        mLlMylike = view.findViewById(R.id.ll_user_mylike);
        mLlSettinng = view.findViewById(R.id.ll_user_setting);
        mLlClear = view.findViewById(R.id.ll_user_clear);
        mBtnExit = view.findViewById(R.id.btn_main_exit);

        // 设置点击事件
        initListener();
        return view;
    }
}
