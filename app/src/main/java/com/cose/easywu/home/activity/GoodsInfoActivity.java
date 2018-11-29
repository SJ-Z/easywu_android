package com.cose.easywu.home.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.db.LikeGoods;
import com.cose.easywu.db.ReleaseGoods;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.home.adapter.HomeFragmentAdapter;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.release.activity.ReleaseActivity;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.DateUtil;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ImageUtils;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;
import com.cose.easywu.widget.MessageDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoodsInfoActivity extends BaseActivity {

    private ImageView mIvBack, mIvUserPhoto, mIvUserSex, mIvGoodsPic1, mIvGoodsPic2, mIvGoodsPic3, mIvLike;
    private TextView mTvTitlePrice, mTvUserNick, mTvUserUpdateTime, mTvPrice, mTvOriginalPrice,
                        mTvGoodsName, mTvGoodsDesc, mTvMsgNum, mTvLeaveMsg, mTvContact, mTvEdit, mTvDelete;
    private LinearLayout mLlOriginalPrice, mLlLeaveMsg, mLlLike, mLlManage;
    private ProgressDialog progressDialog;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private HomeDataBean.NewestInfoBean goods;
    private LikeGoods likeGoods;
    private boolean like = false;
    private String u_id;

    private MyHandler mHandler = new MyHandler(GoodsInfoActivity.this);
    private static int MSG_PICLEN = 0;
    private static int MSG_PIC = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_info);

        initView();
        initData();
        initListener();
    }

    private void initListener() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLlLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goods.getG_u_id().equals(pref.getString("u_id", ""))) {
                    ToastUtil.showMsgOnCenter(GoodsInfoActivity.this, "不能收藏自己的宝贝哦~", Toast.LENGTH_SHORT);
                    return;
                }
                if (like) {
                    like = false;
                    mIvLike.setImageResource(R.drawable.ic_goods_like);
                    likeGoods.delete();
                    // 从服务器删除数据
                    setLikeFromServer();
                } else {
                    like = true;
                    mIvLike.setImageResource(R.drawable.ic_goods_like_press);
                    likeGoods = new LikeGoods(goods.getG_id(), goods.getG_name(), goods.getG_desc(), goods.getG_price(),
                            goods.getG_originalPrice(), goods.getG_pic1(), goods.getG_pic2(), goods.getG_pic3(),
                            goods.getG_state(), goods.getG_like(), goods.getG_updateTime(), goods.getG_t_id(),
                            goods.getG_u_id(), goods.getG_u_nick(), goods.getG_u_photo(), goods.getG_u_sex());
                    likeGoods.save();
                    // 向服务器添加数据
                    setLikeFromServer();
                }
            }
        });
        mLlLeaveMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mTvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goods.getG_u_id().equals(pref.getString("u_id", ""))) {
                    ToastUtil.showMsgOnCenter(GoodsInfoActivity.this, "你就是卖家啦~", Toast.LENGTH_SHORT);
                    return;
                }

            }
        });
        mTvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleEdit();
            }
        });
        mTvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDelete();
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在准备界面...");
        }
        progressDialog.show();
    }

    private void handleEdit() {
        showProgressDialog();
        editor.putBoolean(u_id + "_hasSaveContent", true);
        editor.putString(u_id + "_name", goods.getG_name());
        editor.putString(u_id + "_desc", goods.getG_desc());
        editor.putString(u_id + "_price", String.valueOf(goods.getG_price()));
        editor.putString(u_id + "_originalPrice", String.valueOf(goods.getG_originalPrice()));
        editor.putString(u_id + "_typeId", goods.getG_t_id());
        editor.putString("g_id", goods.getG_id());

        String[] picAddr = {goods.getG_pic1(), goods.getG_pic2(), goods.getG_pic3()};
        loadPicToCache(picAddr);

        editor.apply();
    }

    private void loadPicToCache(final String[] picAddr) {
        int picLen = 1;
        if (picAddr[1] != null) {
            picLen++;
            if (picAddr[2] != null) {
                picLen++;
            }
        }
        editor.putInt(u_id + "_picLen", picLen);
        // 发送消息
        Message message = new Message();
        message.what = MSG_PICLEN;
        message.arg1 = picLen;
        mHandler.sendMessage(message);

        OkHttpClient client = new OkHttpClient();
        for (int i = 0; i < picLen; i++) {
            final int index = i;
            final Request request = new Request.Builder().get()
                    .url(Constant.BASE_PIC_URL + picAddr[i])
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
                            final Bitmap bitmap = ImageUtils.getBitmapFromByte(bytes, 100, 100);
                            String picName = u_id + "_pic" + index;
                            editor.putString(picName, ImageUtils.savePhotoToCache(bitmap, picName));
                            editor.apply();

                            // 发送消息
                            Message message = new Message();
                            message.what = MSG_PIC;
                            mHandler.sendMessage(message);
                        }
                    }
                }
            });
        }
    }

    private void handleDelete() {
        MessageDialog messageDialog = new MessageDialog(GoodsInfoActivity.this, R.style.MessageDialog);
        messageDialog.setTitle("提示").setContent("确认删除该宝贝？")
                .setCancel("删除", new MessageDialog.IOnCancelListener() {
                    @Override
                    public void onCancel(MessageDialog dialog) {
                        // 去服务器删除商品
                        deleteGoodsToServer();
                    }
                }).setConfirm("取消", new MessageDialog.IOnConfirmListener() {
            @Override
            public void onConfirm(MessageDialog dialog) {
                // do nothing
            }
        }).show();
    }

    private void deleteGoodsToServer() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("g_id", goods.getG_id());
            jsonObject.put("u_id", u_id);
            String json = jsonObject.toString();
            HttpUtil.sendPostRequest(Constant.DELETE_GOODS_URL, json, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ToastUtil.showMsgOnCenter(GoodsInfoActivity.this, "删除失败", Toast.LENGTH_SHORT);
                    Log.e("GoodsInfoActivity", "删除商品失败:" + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (null == response.body()) {
                        return;
                    }

                    String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                    final BaseMsg msg = Utility.handleBaseMsgResponse(responseText);
                    if (null == msg) {
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (msg.getCode().equals("1")) {
                                LitePal.where("g_id=?", goods.getG_id()).findFirst(ReleaseGoods.class).delete();
                                ToastUtil.showMsgOnCenter(GoodsInfoActivity.this, "删除成功", Toast.LENGTH_SHORT);
                                Log.e("GoodsInfoActivity", "删除商品成功");
                                finish();
                            } else {
                                ToastUtil.showMsgOnCenter(GoodsInfoActivity.this, "删除失败", Toast.LENGTH_SHORT);
                                Log.e("GoodsInfoActivity", "删除商品失败");
                            }
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setLikeFromServer() {
        String g_id = goods.getG_id();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("g_id", g_id);
            jsonObject.put("u_id", u_id);
            jsonObject.put("like", like);
            String json = jsonObject.toString();
            HttpUtil.sendPostRequest(Constant.SET_LIKE_GOODS_URL, json, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (like) {
                        mIvLike.setImageResource(R.drawable.ic_goods_like);
                        ToastUtil.showMsgOnCenter(GoodsInfoActivity.this, "收藏失败", Toast.LENGTH_SHORT);
                    } else {
                        mIvLike.setImageResource(R.drawable.ic_goods_like_press);
                        ToastUtil.showMsgOnCenter(GoodsInfoActivity.this, "取消收藏失败", Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (null == response.body()) {
                        return;
                    }

                    String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                    final BaseMsg msg = Utility.handleBaseMsgResponse(responseText);
                    if (null == msg) {
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (like) {
                                ToastUtil.showImageToast(GoodsInfoActivity.this, msg.getMsg(),
                                        R.drawable.ic_goods_like_press, Toast.LENGTH_SHORT);
                            } else {
                                ToastUtil.showMsgOnCenter(GoodsInfoActivity.this, msg.getMsg(),
                                        Toast.LENGTH_SHORT);
                            }
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        u_id = pref.getString("u_id", "");
        goods = (HomeDataBean.NewestInfoBean) getIntent().getSerializableExtra(HomeFragmentAdapter.GOODS_BEAN);
        if (goods != null) {
            // 加载图片
            Glide.with(this).load(Constant.BASE_PHOTO_URL + goods.getG_u_photo())
                    .apply(new RequestOptions().placeholder(R.drawable.nav_icon).skipMemoryCache(true))
                    .into(mIvUserPhoto);
            Glide.with(this).load(goods.getG_u_sex()==0?R.drawable.ic_female:R.drawable.ic_male).into(mIvUserSex);
            Glide.with(this).load(Constant.BASE_PIC_URL + goods.getG_pic1())
                    .apply(new RequestOptions().placeholder(R.drawable.pic_loading_goods).skipMemoryCache(true))
                    .into(mIvGoodsPic1);
            if (goods.getG_pic2() != null) {
                Glide.with(this).load(Constant.BASE_PIC_URL + goods.getG_pic2())
                        .apply(new RequestOptions().placeholder(R.drawable.pic_loading_goods).skipMemoryCache(true))
                        .into(mIvGoodsPic2);
            } else {
                mIvGoodsPic2.setVisibility(View.GONE);
            }
            if (goods.getG_pic3() != null) {
                Glide.with(this).load(Constant.BASE_PIC_URL + goods.getG_pic3())
                        .apply(new RequestOptions().placeholder(R.drawable.pic_loading_goods).skipMemoryCache(true))
                        .into(mIvGoodsPic3);
            } else {
                mIvGoodsPic3.setVisibility(View.GONE);
            }
            // 加载文本
            mTvTitlePrice.setText(String.valueOf(goods.getG_price()));
            mTvUserNick.setText(goods.getG_u_nick());
            mTvUserUpdateTime.setText(DateUtil.getDatePoor(goods.getG_updateTime(), new Date()) + "来过");
            mTvPrice.setText(String.valueOf(goods.getG_price()));
            if (goods.getG_originalPrice() > 0) {
                mTvOriginalPrice.setText(String.valueOf(goods.getG_originalPrice()));
                mLlOriginalPrice.setVisibility(View.VISIBLE);
            }
            mTvGoodsName.setText(goods.getG_name());
            mTvGoodsDesc.setText(goods.getG_desc());
            mTvMsgNum.setText("0"); // 设置留言数量

            // 检测是否已收藏该商品
            likeGoods = LitePal.where("g_id=?", goods.getG_id()).findFirst(LikeGoods.class);
            if (likeGoods != null) {
                like = true;
                mIvLike.setImageResource(R.drawable.ic_goods_like_press);
            }

            // 检测是否是商品的发布者
            if (goods.getG_u_id().equals(pref.getString("u_id", ""))) {
                mLlManage.setVisibility(View.VISIBLE);
                mTvContact.setVisibility(View.GONE);
            }
        }
    }

    // 启动ReleaseActivity
    public void startReleaseActivity() {
        progressDialog.dismiss();
        startActivity(new Intent(GoodsInfoActivity.this, ReleaseActivity.class));
    }

    private void initView() {
        mIvBack = findViewById(R.id.iv_goodsInfo_back);
        mIvUserPhoto = findViewById(R.id.iv_goodsInfo_user_photo);
        mTvTitlePrice = findViewById(R.id.tv_goodsInfo_titlebar_price);
        mTvUserNick = findViewById(R.id.tv_goodsInfo_user_nick);
        mIvUserSex = findViewById(R.id.iv_goodsInfo_user_sex);
        mTvUserUpdateTime = findViewById(R.id.tv_goodsInfo_user_updateTime);
        mTvPrice = findViewById(R.id.tv_goodsInfo_price);
        mTvOriginalPrice = findViewById(R.id.tv_goodsInfo_originalPrice);
        mTvGoodsName = findViewById(R.id.tv_goodsInfo_name);
        mTvGoodsDesc = findViewById(R.id.tv_goodsInfo_desc);
        mIvGoodsPic1 = findViewById(R.id.iv_goodsInfo_pic1);
        mIvGoodsPic2 = findViewById(R.id.iv_goodsInfo_pic2);
        mIvGoodsPic3 = findViewById(R.id.iv_goodsInfo_pic3);
        mTvMsgNum = findViewById(R.id.tv_goodsInfo_msgNum);
        mLlOriginalPrice = findViewById(R.id.ll_goodsInfo_originalPrice);
        mIvLike = findViewById(R.id.iv_goodsInfo_like);
        mTvEdit = findViewById(R.id.tv_goodsInfo_edit);
        mTvDelete = findViewById(R.id.tv_goodsInfo_delete);
        mLlLeaveMsg = findViewById(R.id.ll_goodsInfo_leaveMsg);
        mLlLike = findViewById(R.id.ll_goodsInfo_like);
        mTvContact = findViewById(R.id.tv_goodsInfo_contact);
        mLlManage = findViewById(R.id.ll_goodsInfo_manage);

        // 给商品原价添加删除线
        mTvOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private static class MyHandler extends Handler {
        private WeakReference<GoodsInfoActivity> activityWeakReference;
        private int picLen;
        private int count = 0;

        public MyHandler(GoodsInfoActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            GoodsInfoActivity activity = activityWeakReference.get();
            if (activity != null) {
                if (msg.what == MSG_PICLEN) {
                    picLen = msg.arg1;
                } else if (msg.what == MSG_PIC) {
                    count++;
                    if (count == picLen) {
                        activity.startReleaseActivity();
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
