package com.cose.easywu.home.activity;

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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.db.LikeGoods;
import com.cose.easywu.db.ReleaseGoods;
import com.cose.easywu.db.User;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.gson.msg.CommentMsg;
import com.cose.easywu.gson.msg.GoodsMsg;
import com.cose.easywu.home.adapter.CommentExpandAdapter;
import com.cose.easywu.home.adapter.HomeFragmentAdapter;
import com.cose.easywu.home.bean.CommentBean;
import com.cose.easywu.home.bean.CommentDetailBean;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.home.bean.ReplyDetailBean;
import com.cose.easywu.message.activity.ChatActivity;
import com.cose.easywu.release.activity.ReleaseActivity;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.DateUtil;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ImageUtils;
import com.cose.easywu.utils.NestedExpandableListView;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;
import com.cose.easywu.widget.MessageDialog;
import com.hyphenate.chat.EMClient;
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

public class GoodsInfoActivity extends BaseActivity {

    private ImageView mIvBack, mIvUserPhoto, mIvUserSex, mIvGoodsPic1, mIvGoodsPic2, mIvGoodsPic3, mIvLike;
    private TextView mTvTitlePrice, mTvUserNick, mTvUserUpdateTime, mTvPrice, mTvOriginalPrice,
                        mTvGoodsName, mTvGoodsDesc, mTvMsgNum, mTvContact, mTvEdit, mTvDelete;
    private LinearLayout mLlOriginalPrice, mLlLeaveMsg, mLlLike, mLlManage;
    private ScrollView mSv;
    private NestedExpandableListView mElvComment;
    private CommentExpandAdapter adapter;
    private ProgressDialog progressDialog;
    private BottomSheetDialog dialog;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private HomeDataBean.NewestInfoBean goods;
    private LikeGoods likeGoods;
    private boolean like = false;
    private String u_id;
    private User user;
    private List<CommentDetailBean> commentList;
    private int goodsCommentBean_id;

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
                showCommentDialog();
            }
        });
        mTvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goods.getG_u_id().equals(pref.getString("u_id", ""))) {
                    ToastUtil.showMsgOnCenter(GoodsInfoActivity.this, "你就是卖家啦~", Toast.LENGTH_SHORT);
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
        intent.putExtra(EaseConstant.EXTRA_USER_ID, goods.getG_u_id());
        // 传递商品信息
        intent.putExtra("isGoods", true);
        intent.putExtra("goods_id", goods.getG_id());
        intent.putExtra("goods_name", goods.getG_name());
        intent.putExtra("goods_pic", Constant.BASE_PIC_URL + goods.getG_pic1());
        intent.putExtra("goods_price", goods.getG_price());
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
        user = LitePal.where("u_id=?", u_id).findFirst(User.class);

        // 判断进入该Activity是否是由聊天窗口点击进来的
        Intent intent = getIntent();
        if (intent.getBooleanExtra(GoodsMessageHelper.CHATTYPE, false)) {
            // 利用商品id查找本地数据库
            ReleaseGoods releaseGoods = LitePal.where("g_id=?", intent.getStringExtra(GoodsMessageHelper.GOODS_ID)).findFirst(ReleaseGoods.class);
            if (releaseGoods != null) {
                goods = new HomeDataBean.NewestInfoBean(releaseGoods.getG_id(), releaseGoods.getG_name(), releaseGoods.getG_desc(),
                        releaseGoods.getG_price(), releaseGoods.getG_originalPrice(), releaseGoods.getG_pic1(), releaseGoods.getG_pic2(),
                        releaseGoods.getG_pic3(), releaseGoods.getG_state(), releaseGoods.getG_like(), releaseGoods.getG_updateTime(),
                        releaseGoods.getG_t_id(), u_id, user.getU_nick(), user.getU_photo(), user.getU_sex());
                loadGoodsInfo(goods);
            } else {
                // 利用商品id去服务器请求数据
                loadGoodsFromServer(intent.getStringExtra(GoodsMessageHelper.GOODS_ID));
            }
        }

        goods = (HomeDataBean.NewestInfoBean) getIntent().getSerializableExtra(HomeFragmentAdapter.GOODS_BEAN);
        if (goods != null) {
            loadGoodsInfo(goods);
        }
    }

    private void loadGoodsFromServer(String g_id) {
        HttpUtil.sendPostRequest(Constant.GET_GOODS_URL, g_id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.showMsgOnCenter(GoodsInfoActivity.this, "加载商品失败", Toast.LENGTH_SHORT);
                Log.e("GoodsInfoActivity", "加载商品失败:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null == response.body()) {
                    return;
                }

                String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                final GoodsMsg msg = Utility.handleGoodsResponse(responseText);
                if (null == msg) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (msg.getCode().equals("1")) {
                            goods = new HomeDataBean.NewestInfoBean(msg.getGoods().getG_id(), msg.getGoods().getG_name(),
                                    msg.getGoods().getG_desc(), msg.getGoods().getG_price(), msg.getGoods().getG_originalPrice(),
                                    msg.getGoods().getG_pic1(), msg.getGoods().getG_pic2(), msg.getGoods().getG_pic3(),
                                    msg.getGoods().getG_state(), msg.getGoods().getG_like(), new Date(msg.getGoods().getG_updateTime()),
                                    msg.getGoods().getG_t_id(), u_id, user.getU_nick(), user.getU_photo(), user.getU_sex());
                            loadGoodsInfo(goods);
                        } else {
                            ToastUtil.showMsgOnCenter(GoodsInfoActivity.this, "商品已失效", Toast.LENGTH_SHORT);
                            Log.e("GoodsInfoActivity", "商品已失效");
                        }
                    }
                });
            }
        });
    }

    private void loadGoodsInfo(HomeDataBean.NewestInfoBean goods) {
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

        // 向服务器发起请求加载评论
        getCommentFromServer();
    }

    private void getCommentFromServer() {
        String json = "{'g_id':'" + goods.getG_id() + "'}";
        String address = Constant.GOODS_COMMENT_URL;

        HttpUtil.sendPostRequest(address, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mTvMsgNum.setText("加载失败");
                Log.e("GoodsInfoActivity", "评论加载失败==" + e.getMessage());
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
                Log.d("GoodsInfoActivity", "评论加载成功==" + responseText);
            }
        });
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
        mSv = findViewById(R.id.scroll_view_goodsInfo);
        mTvContact = findViewById(R.id.tv_goodsInfo_contact);
        mLlManage = findViewById(R.id.ll_goodsInfo_manage);
        mElvComment = findViewById(R.id.lv_goodsInfo_comment);

        // 给商品原价添加删除线
        mTvOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
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
        final EditText commentText = commentView.findViewById(R.id.dialog_comment_et);
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
                    Toast.makeText(GoodsInfoActivity.this,"评论内容不能为空",Toast.LENGTH_SHORT).show();
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
        jsonObject.put("g_id", goods.getG_id());
        jsonObject.put("u_id", u_id);
        String address = Constant.GOODS_ADD_COMMENT_URL;

        HttpUtil.sendPostRequest(address, jsonObject.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mTvMsgNum.setText("留言失败");
                Log.e("GoodsInfoActivity", "留言失败==" + e.getMessage());
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
                                    user.getU_nick(), user.getU_photo(), commentContent, msg.getTime());
                            adapter.addTheCommentData(commentDetailBean);
                            mElvComment.expandGroup(commentList.size() - 1);
                            mTvMsgNum.setText(String.valueOf(commentList.size()));
                            ToastUtil.showMsgOnCenter(GoodsInfoActivity.this, "留言成功", Toast.LENGTH_SHORT);
                            Log.d("GoodsInfoActivity", "留言成功");
                        } else {
                            ToastUtil.showMsgOnCenter(GoodsInfoActivity.this, "留言失败", Toast.LENGTH_SHORT);
                            Log.d("GoodsInfoActivity", "留言失败");
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
        final EditText commentText = commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = commentView.findViewById(R.id.dialog_comment_bt);
        if (childPosition == -1) {
            commentText.setHint("回复 " + commentList.get(groupPosition).getNickName() + " 的评论:");
        } else {
            commentText.setHint("回复 " + commentList.get(groupPosition).getReplyList().get(childPosition).getNickName() + " 的评论:");
        }
        dialog.setContentView(commentView);
        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String replyContent;
                if (childPosition != -1) {
                    StringBuffer sb = new StringBuffer("回复@");
                    sb.append(commentList.get(groupPosition).getReplyList().get(childPosition).getNickName())
                            .append(":").append(commentText.getText().toString().trim());
                    replyContent = sb.toString();
                } else {
                    replyContent = commentText.getText().toString().trim();
                }
                if(!TextUtils.isEmpty(replyContent)){
                    dialog.dismiss();
                    // 发送回复数据到服务器
                    sendReplyToServer(replyContent, groupPosition);
                } else {
                    Toast.makeText(GoodsInfoActivity.this,"回复内容不能为空",Toast.LENGTH_SHORT).show();
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

    // 发送回复数据到服务器
    private void sendReplyToServer(final String replyContent, final int groupPosition) {
        final CommentDetailBean commentDetailBean = commentList.get(groupPosition);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("u_id", u_id);
            jsonObject.put("reply", replyContent);
            jsonObject.put("comment_id", commentDetailBean.getId());
            String json = jsonObject.toString();
            HttpUtil.sendPostRequest(Constant.GOODS_ADD_REPLY_URL, json, new Callback() {
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
                                        user.getU_nick(), commentDetailBean.getId(),
                                        replyContent, msg.getTime());
                                adapter.addTheReplyData(replyDetailBean, groupPosition);
                                ToastUtil.showMsgOnCenter(GoodsInfoActivity.this, "回复成功", Toast.LENGTH_SHORT);
                                Log.d("GoodsInfoActivity", "回复成功");
                            } else {
                                ToastUtil.showMsgOnCenter(GoodsInfoActivity.this, "回复失败", Toast.LENGTH_SHORT);
                                Log.d("GoodsInfoActivity", "回复失败");
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
