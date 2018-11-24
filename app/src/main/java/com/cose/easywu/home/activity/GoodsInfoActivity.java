package com.cose.easywu.home.activity;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.home.adapter.HomeFragmentAdapter;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.DateUtil;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GoodsInfoActivity extends BaseActivity {

    private ImageView mIvBack, mIvUserPhoto, mIvUserSex, mIvGoodsPic1, mIvGoodsPic2, mIvGoodsPic3, mIvLike;
    private TextView mTvTitlePrice, mTvUserNick, mTvUserUpdateTime, mTvPrice, mTvOriginalPrice,
                        mTvGoodsName, mTvGoodsDesc, mTvMsgNum, mTvLeaveMsg, mTvContact;
    private LinearLayout mLlOriginalPrice, mLlLeaveMsg, mLlLike;

    private SharedPreferences pref;
    private HomeDataBean.NewestInfoBean goods;
    private LikeGoods likeGoods;
    private boolean like = false;

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
                            goods.getG_state(), goods.getG_like(), goods.getG_updateTime(), goods.getG_u_id(),
                            goods.getG_u_nick(), goods.getG_u_photo(), goods.getG_u_sex());
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

            }
        });
    }

    private void setLikeFromServer() {
        String g_id = goods.getG_id();
        String u_id = pref.getString("u_id", "");
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
        goods = (HomeDataBean.NewestInfoBean) getIntent().getSerializableExtra(HomeFragmentAdapter.GOODS_BEAN);
        if (goods != null) {
            // 加载图片
            Glide.with(this).load(Constant.BASE_PHOTO_URL + goods.getG_u_photo())
                    .apply(new RequestOptions().placeholder(R.drawable.nav_icon))
                    .into(mIvUserPhoto);
            Glide.with(this).load(goods.getG_u_sex()==0?R.drawable.ic_female:R.drawable.ic_male).into(mIvUserSex);
            Glide.with(this).load(Constant.BASE_PIC_URL + goods.getG_pic1())
                    .apply(new RequestOptions().placeholder(R.drawable.pic_loading_goods))
                    .into(mIvGoodsPic1);
            if (goods.getG_pic2() != null) {
                Glide.with(this).load(Constant.BASE_PIC_URL + goods.getG_pic2())
                        .apply(new RequestOptions().placeholder(R.drawable.pic_loading_goods))
                        .into(mIvGoodsPic2);
            } else {
                mIvGoodsPic2.setVisibility(View.GONE);
            }
            if (goods.getG_pic3() != null) {
                Glide.with(this).load(Constant.BASE_PIC_URL + goods.getG_pic3())
                        .apply(new RequestOptions().placeholder(R.drawable.pic_loading_goods))
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
        }
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
        mLlLeaveMsg = findViewById(R.id.ll_goodsInfo_leaveMsg);
        mLlLike = findViewById(R.id.ll_goodsInfo_like);
        mTvContact = findViewById(R.id.tv_goodsInfo_contact);

        // 给商品原价添加删除线
        mTvOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }
}
