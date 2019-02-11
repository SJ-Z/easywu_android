package com.cose.easywu.find.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.db.BuyGoods;
import com.cose.easywu.db.LikeFindGoods;
import com.cose.easywu.db.LikeFindPeople;
import com.cose.easywu.db.LikeGoods;
import com.cose.easywu.db.ReleaseFindGoods;
import com.cose.easywu.db.ReleaseFindPeople;
import com.cose.easywu.db.ReleaseGoods;
import com.cose.easywu.db.User;
import com.cose.easywu.find.adapter.FindFragmentAdapter;
import com.cose.easywu.find.bean.FindDataBean;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.gson.msg.CommentMsg;
import com.cose.easywu.gson.msg.GoodsMsg;
import com.cose.easywu.home.activity.GoodsInfoActivity;
import com.cose.easywu.home.adapter.CommentExpandAdapter;
import com.cose.easywu.home.adapter.HomeFragmentAdapter;
import com.cose.easywu.home.bean.CommentBean;
import com.cose.easywu.home.bean.CommentDetailBean;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.home.bean.ReplyDetailBean;
import com.cose.easywu.message.activity.ChatActivity;
import com.cose.easywu.release.activity.ReleaseActivity;
import com.cose.easywu.release.activity.ReleaseFindGoodsActivity;
import com.cose.easywu.release.activity.ReleaseFindPeopleActivity;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.DateUtil;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ImageUtils;
import com.cose.easywu.utils.NestedExpandableListView;
import com.cose.easywu.utils.NoEmojiEditText;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;
import com.cose.easywu.widget.MessageDialog;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.model.GoodsMessageHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FindGoodsInfoActivity extends BaseActivity {

    private ImageView mIvLoading;
    private RelativeLayout mRlAll, mRlBottomBar;
    private ImageView mIvBack, mIvUserPhoto, mIvUserSex, mIvGoodsPic1, mIvGoodsPic2, mIvGoodsPic3, mIvLike;
    private TextView mTvTitle, mTvUserNick, mTvUserUpdateTime, mTvGoodsName, mTvGoodsDesc,
            mTvMsgNum, mTvContact, mTvEdit, mTvDelete;
    private LinearLayout mLlLeaveMsg, mLlLike, mLlManage;
    private ScrollView mSv;
    private NestedExpandableListView mElvComment;
    private CommentExpandAdapter adapter;
    private ProgressDialog progressDialog;
    private BottomSheetDialog dialog;

    private boolean isFindGoods; // 区分寻找失物和寻找失主的标志位
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private FindDataBean.FindNewestInfo goods;
    private LikeFindGoods likeFindGoods;
    private LikeFindPeople likeFindPeople;
    private boolean like = false;
    private String u_id;
    private User user;
    private List<CommentDetailBean> commentList;
    private int goodsCommentBean_id;

    private LocalBroadcastManager localBroadcastManager;

    private FindGoodsInfoActivity.MyHandler mHandler = new FindGoodsInfoActivity.MyHandler(FindGoodsInfoActivity.this);
    private static int MSG_PICLEN = 0;
    private static int MSG_PIC = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_goods_info);

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
                if (goods.getFg_u_id().equals(u_id)) {
                    ToastUtil.showMsgOnCenter(FindGoodsInfoActivity.this, "不能收藏自己发布的内容哦~", Toast.LENGTH_SHORT);
                    return;
                }
                if (like) {
                    like = false;
                    mIvLike.setImageResource(R.drawable.ic_goods_like);
                    if (isFindGoods) {
                        likeFindGoods.delete();
                    } else {
                        likeFindPeople.delete();
                    }
                    // 从服务器删除数据
                    setLikeFromServer();
                } else {
                    like = true;
                    mIvLike.setImageResource(R.drawable.ic_goods_like_press);
                    if (isFindGoods) {
                        likeFindGoods = new LikeFindGoods(goods.getFg_id(), goods.getFg_name(), goods.getFg_desc(),
                                goods.getFg_pic1(), goods.getFg_pic2(), goods.getFg_pic3(), goods.getFg_state(),
                                goods.getFg_like(), goods.getFg_updateTime(), goods.getFg_ft_id(), goods.getFg_u_id(),
                                goods.getFg_u_nick(), goods.getFg_u_photo(), goods.getFg_u_sex());
                        likeFindGoods.save();
                    } else {
                        likeFindPeople = new LikeFindPeople(goods.getFg_id(), goods.getFg_name(), goods.getFg_desc(),
                                goods.getFg_pic1(), goods.getFg_pic2(), goods.getFg_pic3(), goods.getFg_state(),
                                goods.getFg_like(), goods.getFg_updateTime(), goods.getFg_ft_id(), goods.getFg_u_id(),
                                goods.getFg_u_nick(), goods.getFg_u_photo(), goods.getFg_u_sex());
                        likeFindPeople.save();
                    }
                    // 向服务器添加数据
                    setLikeFromServer();
                }
            }
        });
        mLlLeaveMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog();
            }
        });
        mTvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goods.getFg_u_id().equals(u_id)) {
                    ToastUtil.showMsgOnCenter(FindGoodsInfoActivity.this, "你就是发布者哦~", Toast.LENGTH_SHORT);
                    return;
                } else {
                    chatWithSeller();
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

    private void chatWithSeller() {
        Intent intent = new Intent(this, ChatActivity.class);
        // 传递参数，会话id即环信id
        intent.putExtra(EaseConstant.EXTRA_USER_ID, goods.getFg_u_id());
        // 传递商品信息
        intent.putExtra("isGoods", true);
        intent.putExtra(GoodsMessageHelper.GOODS_ID, goods.getFg_id());
        intent.putExtra(GoodsMessageHelper.GOODS_NAME, goods.getFg_name());
        intent.putExtra(GoodsMessageHelper.GOODS_PIC, Constant.BASE_FIND_PIC_URL + goods.getFg_pic1());
//        intent.putExtra(GoodsMessageHelper.GOODS_PRICE, goods.getG_price());
        startActivity(intent);
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
        if (isFindGoods) {
            editor.putBoolean(u_id + "_hasSaveContent_findGoods", true);
            editor.putString(u_id + "_name_findGoods", goods.getFg_name());
            editor.putString(u_id + "_desc_findGoods", goods.getFg_desc());
            editor.putString(u_id + "_findTypeId_findGoods", goods.getFg_ft_id());
            editor.putString("fg_id_findGoods", goods.getFg_id());
        } else {
            editor.putBoolean(u_id + "_hasSaveContent_findPeople", true);
            editor.putString(u_id + "_name_findPeople", goods.getFg_name());
            editor.putString(u_id + "_desc_findPeople", goods.getFg_desc());
            editor.putString(u_id + "_findTypeId_findPeople", goods.getFg_ft_id());
            editor.putString("fg_id_findPeople", goods.getFg_id());
        }

        String[] picAddr = {goods.getFg_pic1(), goods.getFg_pic2(), goods.getFg_pic3()};
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
        if (isFindGoods) {
            editor.putInt(u_id + "_findGoods_picLen", picLen);
        } else {
            editor.putInt(u_id + "_findPeople_picLen", picLen);
        }
        // 发送消息
        Message message = new Message();
        message.what = MSG_PICLEN;
        message.arg1 = picLen;
        mHandler.sendMessage(message);

        OkHttpClient client = new OkHttpClient();
        for (int i = 0; i < picLen; i++) {
            final int index = i;
            final Request request = new Request.Builder().get()
                    .url(Constant.BASE_FIND_PIC_URL + picAddr[i])
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
                            String picName;
                            if (isFindGoods) {
                                picName = u_id + "_findGoods_pic" + index;
                            } else {
                                picName = u_id + "_findPeople_pic" + index;
                            }
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
        MessageDialog messageDialog = new MessageDialog(FindGoodsInfoActivity.this, R.style.MessageDialog);
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
            jsonObject.put("fg_id", goods.getFg_id());
            jsonObject.put("u_id", u_id);
            if (isFindGoods) {
                jsonObject.put("isFindGoods", true);
            } else {
                jsonObject.put("isFindGoods", false);
            }
            HttpUtil.sendPostRequest(Constant.DELETE_FIND_GOODS_URL, jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("FindGoodsInfoActivity", "删除商品失败:" + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showMsgOnCenter(FindGoodsInfoActivity.this, "删除失败", Toast.LENGTH_SHORT);
                        }
                    });
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
                                if (isFindGoods) {
                                    LitePal.where("fg_id=?", goods.getFg_id()).findFirst(ReleaseFindGoods.class).delete();
                                    // 发送广播
                                    localBroadcastManager.sendBroadcast(new Intent(Constant.RELEASE_NEW_RELEASE_FIND_GOODS));
                                } else {
                                    LitePal.where("fg_id=?", goods.getFg_id()).findFirst(ReleaseFindPeople.class).delete();
                                    // 发送广播
                                    localBroadcastManager.sendBroadcast(new Intent(Constant.RELEASE_NEW_RELEASE_FIND_PEOPLE));
                                }
                                ToastUtil.showMsgOnCenter(FindGoodsInfoActivity.this, "删除成功", Toast.LENGTH_SHORT);
                                Log.e("FindGoodsInfoActivity", "删除失物招领成功");
                                finish();
                            } else {
                                ToastUtil.showMsgOnCenter(FindGoodsInfoActivity.this, "删除失败", Toast.LENGTH_SHORT);
                                Log.e("FindGoodsInfoActivity", "删除失物招领失败");
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
        String fg_id = goods.getFg_id();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fg_id", fg_id);
            jsonObject.put("u_id", u_id);
            jsonObject.put("like", like);
            if (isFindGoods) {
                jsonObject.put("isFindGoods", true);
            } else {
                jsonObject.put("isFindGoods", false);
            }
            String json = jsonObject.toString();
            HttpUtil.sendPostRequest(Constant.SET_LIKE_FIND_GOODS_URL, json, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (like) {
                                mIvLike.setImageResource(R.drawable.ic_goods_like);
                                ToastUtil.showMsgOnCenter(FindGoodsInfoActivity.this, "收藏失败", Toast.LENGTH_SHORT);
                            } else {
                                mIvLike.setImageResource(R.drawable.ic_goods_like_press);
                                ToastUtil.showMsgOnCenter(FindGoodsInfoActivity.this, "取消收藏失败", Toast.LENGTH_SHORT);
                            }
                        }
                    });
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
                                ToastUtil.showImageToast(FindGoodsInfoActivity.this, msg.getMsg(),
                                        R.drawable.ic_goods_like_press, Toast.LENGTH_SHORT);
                            } else {
                                ToastUtil.showMsgOnCenter(FindGoodsInfoActivity.this, msg.getMsg(),
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
        user = LitePal.where("u_id=?", u_id).findFirst(User.class);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        // 判断进入该Activity的intent是否携带指定信息
        Intent intent = getIntent();
        isFindGoods = intent.getBooleanExtra("isFindGoods", false);
        if (intent.getBooleanExtra(GoodsMessageHelper.CHATTYPE, false)
                || intent.getBooleanExtra(GoodsMessageHelper.ConfirmGoodsOrderType, false)
                || intent.getBooleanExtra(GoodsMessageHelper.RefuseGoodsOrderType, false)) {
            // 利用商品id查找本地数据库
            if (isFindGoods) {
                ReleaseFindGoods releaseFindGoods = LitePal.where("fg_id=?",
                        intent.getStringExtra(GoodsMessageHelper.GOODS_ID)).findFirst(ReleaseFindGoods.class);
                if (releaseFindGoods != null) {
                    goods = new FindDataBean.FindNewestInfo(releaseFindGoods.getFg_id(), releaseFindGoods.getFg_name(),
                            releaseFindGoods.getFg_desc(), releaseFindGoods.getFg_pic1(), releaseFindGoods.getFg_pic2(),
                            releaseFindGoods.getFg_pic3(), releaseFindGoods.getFg_state(), releaseFindGoods.getFg_like(),
                            releaseFindGoods.getFg_updateTime(), releaseFindGoods.getFg_ft_id(), u_id,
                            user.getU_nick(), user.getU_photo(), user.getU_sex());
                    loadGoodsInfo(goods);
                } else {
                    // 利用商品id去服务器请求数据
//                    loadGoodsFromServer(intent.getStringExtra(GoodsMessageHelper.GOODS_ID));
                }
            } else {
                ReleaseFindPeople releaseFindPeople = LitePal.where("fg_id=?",
                        intent.getStringExtra(GoodsMessageHelper.GOODS_ID)).findFirst(ReleaseFindPeople.class);
                if (releaseFindPeople != null) {
                    goods = new FindDataBean.FindNewestInfo(releaseFindPeople.getFg_id(), releaseFindPeople.getFg_name(),
                            releaseFindPeople.getFg_desc(), releaseFindPeople.getFg_pic1(), releaseFindPeople.getFg_pic2(),
                            releaseFindPeople.getFg_pic3(), releaseFindPeople.getFg_state(), releaseFindPeople.getFg_like(),
                            releaseFindPeople.getFg_updateTime(), releaseFindPeople.getFg_ft_id(), u_id,
                            user.getU_nick(), user.getU_photo(), user.getU_sex());
                    loadGoodsInfo(goods);
                } else {
                    // 利用商品id去服务器请求数据
//                    loadGoodsFromServer(intent.getStringExtra(GoodsMessageHelper.GOODS_ID));
                }
            }
        } else {
            goods = (FindDataBean.FindNewestInfo) getIntent().getSerializableExtra(FindFragmentAdapter.GOODS_BEAN);
            if (goods != null) {
                loadGoodsInfo(goods);
            } else {
            }
        }
    }

//    private void loadGoodsFromServer(String g_id) {
//        HttpUtil.sendPostRequest(Constant.GET_GOODS_URL, g_id, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                ToastUtil.showMsgOnCenter(FindGoodsInfoActivity.this, "加载商品失败", Toast.LENGTH_SHORT);
//                Log.e("FindGoodsInfoActivity", "加载商品失败:" + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (null == response.body()) {
//                    return;
//                }
//
//                String responseText = URLDecoder.decode(response.body().string(), "utf-8");
//                final GoodsMsg msg = Utility.handleGoodsResponse(responseText);
//                if (null == msg) {
//                    return;
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (msg.getCode().equals("1")) {
//                            goods = new HomeDataBean.NewestInfoBean(msg.getGoods().getG_id(), msg.getGoods().getG_name(),
//                                    msg.getGoods().getG_desc(), msg.getGoods().getG_price(), msg.getGoods().getG_originalPrice(),
//                                    msg.getGoods().getG_pic1(), msg.getGoods().getG_pic2(), msg.getGoods().getG_pic3(),
//                                    msg.getGoods().getG_state(), msg.getGoods().getG_like(), new Date(msg.getGoods().getG_updateTime()),
//                                    msg.getGoods().getG_t_id(), u_id, user.getU_nick(), user.getU_photo(), user.getU_sex());
//                            loadGoodsInfo(goods);
//                        } else {
//                            ToastUtil.showMsgOnCenter(FindGoodsInfoActivity.this, "商品已失效", Toast.LENGTH_SHORT);
//                            Log.e("FindGoodsInfoActivity", "商品已失效");
//                        }
//                    }
//                });
//            }
//        });
//    }

    private void loadGoodsInfo(FindDataBean.FindNewestInfo goods) {
        // 加载图片
        Glide.with(this).load(Constant.BASE_PHOTO_URL + goods.getFg_u_photo())
                .apply(new RequestOptions().placeholder(R.drawable.nav_icon).skipMemoryCache(true))
                .into(mIvUserPhoto);
        Glide.with(this).load(goods.getFg_u_sex()==0?R.drawable.ic_female:R.drawable.ic_male).into(mIvUserSex);
        Glide.with(this).load(Constant.BASE_FIND_PIC_URL + goods.getFg_pic1())
                .apply(new RequestOptions().placeholder(R.drawable.pic_loading_goods).skipMemoryCache(true))
                .into(mIvGoodsPic1);
        if (goods.getFg_pic2() != null) {
            Glide.with(this).load(Constant.BASE_FIND_PIC_URL + goods.getFg_pic2())
                    .apply(new RequestOptions().placeholder(R.drawable.pic_loading_goods).skipMemoryCache(true))
                    .into(mIvGoodsPic2);
        } else {
            mIvGoodsPic2.setVisibility(View.GONE);
        }
        if (goods.getFg_pic3() != null) {
            Glide.with(this).load(Constant.BASE_FIND_PIC_URL + goods.getFg_pic3())
                    .apply(new RequestOptions().placeholder(R.drawable.pic_loading_goods).skipMemoryCache(true))
                    .into(mIvGoodsPic3);
        } else {
            mIvGoodsPic3.setVisibility(View.GONE);
        }
        // 加载文本
        mTvUserNick.setText(goods.getFg_u_nick());
        mTvUserUpdateTime.setText(DateUtil.getDatePoor(goods.getFg_updateTime(), new Date()) + "来过");
        mTvGoodsName.setText(goods.getFg_name());
        mTvGoodsDesc.setText(goods.getFg_desc());

        // 检测是否已收藏该商品
        if (isFindGoods) {
            mTvTitle.setText("寻物启示");
            likeFindGoods = LitePal.where("fg_id=?", goods.getFg_id()).findFirst(LikeFindGoods.class);
            if (likeFindGoods != null) {
                like = true;
                mIvLike.setImageResource(R.drawable.ic_goods_like_press);
            }
        } else {
            mTvTitle.setText("失物招领");
            likeFindPeople = LitePal.where("fg_id=?", goods.getFg_id()).findFirst(LikeFindPeople.class);
            if (likeFindPeople != null) {
                like = true;
                mIvLike.setImageResource(R.drawable.ic_goods_like_press);
            }
        }

        // 检测是否是商品的发布者
        if (goods.getFg_u_id().equals(u_id)) {
            mLlLike.setVisibility(View.GONE);
            mLlManage.setVisibility(View.VISIBLE);
            mTvContact.setVisibility(View.GONE);
        }

        // 向服务器发起请求加载评论
        getCommentFromServer();

        mIvLoading.setVisibility(View.GONE);
        mRlAll.setVisibility(View.VISIBLE);

        if (goods.getFg_state() != 0) {
            ToastUtil.showMsgOnCenter(this, "该内容已失效", Toast.LENGTH_SHORT);
            mRlBottomBar.setVisibility(View.GONE);
        }
    }

    private void getCommentFromServer() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fg_id", goods.getFg_id());
            if (isFindGoods) {
                jsonObject.put("isFindGoods", true);
            } else {
                jsonObject.put("isFindGoods", false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtil.sendPostRequest(Constant.FIND_GOODS_COMMENT_URL, jsonObject.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mTvMsgNum.setText("加载失败");
                Log.e("FindGoodsInfoActivity", "评论加载失败==" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null == response.body()) {
                    return;
                }
                // 解析数据
                String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                CommentBean commentBean = Utility.handleCommentResponse(responseText);
                if (commentBean != null) {
                    goodsCommentBean_id = commentBean.getId();
                    commentList = commentBean.getList();
                } else {
                    goodsCommentBean_id = -1;
                    commentList = new ArrayList<>();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initExpandableListView();
                        mSv.scrollTo(0, 0);
                    }
                });
                Log.d("FindGoodsInfoActivity", "评论加载成功==" + responseText);
            }
        });
    }

    // 启动ReleaseActivity
    public void startReleaseActivity() {
        progressDialog.dismiss();
        if (isFindGoods) {
            startActivity(new Intent(FindGoodsInfoActivity.this, ReleaseFindGoodsActivity.class));
        } else {
            startActivity(new Intent(FindGoodsInfoActivity.this, ReleaseFindPeopleActivity.class));
        }
    }

    private void initView() {
        mIvLoading = findViewById(R.id.iv_goodInfo_loading);
        mRlAll = findViewById(R.id.rl_goodsInfo_all);
        mRlBottomBar = findViewById(R.id.rl_goodsInfo_bottombar);
        mIvBack = findViewById(R.id.iv_goodsInfo_back);
        mIvUserPhoto = findViewById(R.id.iv_goodsInfo_user_photo);
        mTvTitle = findViewById(R.id.tv_goodsInfo_titlebar);
        mTvUserNick = findViewById(R.id.tv_goodsInfo_user_nick);
        mIvUserSex = findViewById(R.id.iv_goodsInfo_user_sex);
        mTvUserUpdateTime = findViewById(R.id.tv_goodsInfo_user_updateTime);
        mTvGoodsName = findViewById(R.id.tv_goodsInfo_name);
        mTvGoodsDesc = findViewById(R.id.tv_goodsInfo_desc);
        mIvGoodsPic1 = findViewById(R.id.iv_goodsInfo_pic1);
        mIvGoodsPic2 = findViewById(R.id.iv_goodsInfo_pic2);
        mIvGoodsPic3 = findViewById(R.id.iv_goodsInfo_pic3);
        mTvMsgNum = findViewById(R.id.tv_goodsInfo_msgNum);
        mIvLike = findViewById(R.id.iv_goodsInfo_like);
        mTvEdit = findViewById(R.id.tv_goodsInfo_edit);
        mTvDelete = findViewById(R.id.tv_goodsInfo_delete);
        mLlLeaveMsg = findViewById(R.id.ll_goodsInfo_leaveMsg);
        mLlLike = findViewById(R.id.ll_goodsInfo_like);
        mSv = findViewById(R.id.scroll_view_goodsInfo);
        mTvContact = findViewById(R.id.tv_goodsInfo_contact);
        mLlManage = findViewById(R.id.ll_goodsInfo_manage);
        mElvComment = findViewById(R.id.lv_goodsInfo_comment);

        Glide.with(this).load(R.drawable.gif_loading).apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)).into(mIvLoading);
        mIvLoading.setVisibility(View.VISIBLE);
        mRlAll.setVisibility(View.GONE);
    }

    /**
     * 初始化评论和回复列表
     */
    private void initExpandableListView(){
        mElvComment.setGroupIndicator(null);
        //默认展开所有回复
        adapter = new CommentExpandAdapter(this, commentList);
        mElvComment.setAdapter(adapter);
        int size = commentList.size();
        mTvMsgNum.setText(String.valueOf(size));
        for(int i = 0; i < size; i++){
            mElvComment.expandGroup(i);
        }
        mElvComment.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                showReplyDialog(groupPosition);
//                boolean isExpanded = expandableListView.isGroupExpanded(groupPosition);
//                Log.e("GoodsInfoActivity", "onGroupClick: 当前的评论id>>>"+commentList.get(groupPosition).getId());
//
//                if(isExpanded) {
//                    expandableListView.collapseGroup(groupPosition);
//                } else {
//                    expandableListView.expandGroup(groupPosition, true);
//                }

                return true; // 返回true表示点击评论后，回复列表不自动展开
            }
        });

        mElvComment.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                showReplyDialog(groupPosition, childPosition);
                return false;
            }
        });
    }

    // 弹出评论框
    private void showCommentDialog() {
        dialog = new BottomSheetDialog(this);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout,null);
        final NoEmojiEditText commentText = commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = commentView.findViewById(R.id.dialog_comment_bt);
        dialog.setContentView(commentView);
        /**
         * 解决BottomSheetDialog显示不全的情况
         */
        View parent = (View) commentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        commentView.measure(0,0);
        behavior.setPeekHeight(commentView.getMeasuredHeight());

        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentContent = commentText.getText().toString().trim();
                if(!TextUtils.isEmpty(commentContent)) {
                    dialog.dismiss();
                    addTheCommentData(commentContent);
                } else {
                    Toast.makeText(FindGoodsInfoActivity.this,"评论内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence)){
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                }else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }

    // 向服务器添加评论数据
    private void addTheCommentData(final String commentContent) {
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        jsonObject.put("comment", commentContent);
        jsonObject.put("gc_id", goodsCommentBean_id);
        jsonObject.put("fg_id", goods.getFg_id());
        jsonObject.put("u_id", u_id);
        jsonObject.put("fg_u_id", goods.getFg_u_id());
        jsonObject.put("fg_name", goods.getFg_name());
        if (isFindGoods) {
            jsonObject.put("isFindGoods", true);
        } else {
            jsonObject.put("isFindGoods", false);
        }

        HttpUtil.sendPostRequest(Constant.FIND_GOODS_ADD_COMMENT_URL, jsonObject.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mTvMsgNum.setText("留言失败");
                Log.e("FindGoodsInfoActivity", "留言失败==" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null == response.body()) {
                    return;
                }
                // 解析数据
                String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                final CommentMsg msg = Utility.handleMakeCommentResponse(responseText);
                if (null == msg) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("1".equals(msg.getCode())) {
                            CommentDetailBean commentDetailBean = new CommentDetailBean(msg.getId(),
                                    user.getU_id(), user.getU_nick(), user.getU_photo(), commentContent, msg.getTime());
                            adapter.addTheCommentData(commentDetailBean);
                            mElvComment.expandGroup(commentList.size() - 1);
                            mTvMsgNum.setText(String.valueOf(commentList.size()));
                            ToastUtil.showMsgOnCenter(FindGoodsInfoActivity.this, "留言成功", Toast.LENGTH_SHORT);
                            Log.d("FindGoodsInfoActivity", "留言成功");
                        } else {
                            ToastUtil.showMsgOnCenter(FindGoodsInfoActivity.this, "留言失败", Toast.LENGTH_SHORT);
                            Log.d("FindGoodsInfoActivity", "留言失败");
                        }
                    }
                });
            }
        });
    }

    private void showReplyDialog(final int groupPosition) {
        showReplyDialog(groupPosition, -1);
    }

    // 弹出回复框
    private void showReplyDialog(final int groupPosition, final int childPosition) {
        dialog = new BottomSheetDialog(this);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout,null);
        final NoEmojiEditText commentText = commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = commentView.findViewById(R.id.dialog_comment_bt);
        if (childPosition == -1) {
            commentText.setHint("回复 " + commentList.get(groupPosition).getNickName() + " 的评论:");
        } else {
            if (u_id.equals(commentList.get(groupPosition).getReplyList().get(childPosition).getUid())) {
                ToastUtil.showMsgOnCenter(FindGoodsInfoActivity.this, "不能回复自己哦~", Toast.LENGTH_SHORT);
                return;
            }
            commentText.setHint("回复 " + commentList.get(groupPosition).getReplyList().get(childPosition).getNickName() + " 的评论:");
        }
        dialog.setContentView(commentView);
        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String replyContent = commentText.getText().toString().trim();
                String origin_uid;
                if (TextUtils.isEmpty(replyContent)) {
                    Toast.makeText(FindGoodsInfoActivity.this,"回复内容不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (childPosition != -1) {
                    StringBuffer sb = new StringBuffer("回复@");
                    sb.append(commentList.get(groupPosition).getReplyList().get(childPosition).getNickName())
                            .append(":").append(replyContent);
                    replyContent = sb.toString();
                    origin_uid = commentList.get(groupPosition).getReplyList().get(childPosition).getUid();
                } else {
                    origin_uid = commentList.get(groupPosition).getUid();
                }
                dialog.dismiss();
                // 发送回复数据到服务器
                sendReplyToServer(replyContent, groupPosition, origin_uid);
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence)){
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                }else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }

    // 发送回复数据到服务器
    private void sendReplyToServer(final String replyContent, final int groupPosition, String origin_uid) {
        final CommentDetailBean commentDetailBean = commentList.get(groupPosition);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("u_id", u_id);
            jsonObject.put("reply", replyContent);
            jsonObject.put("comment_id", commentDetailBean.getId());
            jsonObject.put("origin_uid", origin_uid);
            jsonObject.put("fg_id", goods.getFg_id());
            if (isFindGoods) {
                jsonObject.put("isFindGoods", true);
            } else {
                jsonObject.put("isFindGoods", false);
            }
            HttpUtil.sendPostRequest(Constant.FIND_GOODS_ADD_REPLY_URL, jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (null == response.body()) {
                        return;
                    }

                    String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                    final CommentMsg msg = Utility.handleMakeCommentResponse(responseText);
                    if (null == msg) {
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ("1".equals(msg.getCode())) {
                                ReplyDetailBean replyDetailBean = new ReplyDetailBean(msg.getId(),
                                        user.getU_id(), user.getU_nick(), commentDetailBean.getId(),
                                        replyContent, msg.getTime());
                                adapter.addTheReplyData(replyDetailBean, groupPosition);
                                ToastUtil.showMsgOnCenter(FindGoodsInfoActivity.this, "回复成功", Toast.LENGTH_SHORT);
                                Log.d("FindGoodsInfoActivity", "回复成功");
                            } else {
                                ToastUtil.showMsgOnCenter(FindGoodsInfoActivity.this, "回复失败", Toast.LENGTH_SHORT);
                                Log.d("FindGoodsInfoActivity", "回复失败");
                            }
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<FindGoodsInfoActivity> activityWeakReference;
        private int picLen;
        private int count = 0;

        public MyHandler(FindGoodsInfoActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            FindGoodsInfoActivity activity = activityWeakReference.get();
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
