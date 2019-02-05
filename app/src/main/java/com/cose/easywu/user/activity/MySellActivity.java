package com.cose.easywu.user.activity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.db.ReleaseGoods;
import com.cose.easywu.db.SellGoods;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.user.adapter.MySellAdapter;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;
import com.cose.easywu.widget.MessageDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MySellActivity extends BaseActivity {

    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRv;
    private LinearLayout mLlNoGoods;
    private ImageView mIvBack;
    private TextView mTvEarn;

    private MySellAdapter adapter;
    private List<SellGoods> sellGoodsList;
    private String u_id;
    private double myEarnMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sell);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mSwipeRefresh = findViewById(R.id.swipe_refresh_mysell);
        mRv = findViewById(R.id.rv_mysell);
        mLlNoGoods = findViewById(R.id.ll_mysell_nogoods);
        mIvBack = findViewById(R.id.iv_mysell_back);
        mTvEarn = findViewById(R.id.tv_mysell_earn);

        if (LitePal.findAll(SellGoods.class).size() == 0) {
            mLlNoGoods.setVisibility(View.VISIBLE);
            mSwipeRefresh.setVisibility(View.GONE);
        } else {
            // 设置布局管理器
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            mRv.setLayoutManager(manager);
        }
    }

    public void initData() {
        sellGoodsList = LitePal.order("g_updateTime desc").find(SellGoods.class);
        // 更新“在简物赚了xx元”的价格
        myEarnMoney = 0.0;
        for (SellGoods sellGoods : sellGoodsList) {
            if (sellGoods.getG_state() == 1) { // 1状态为已卖出（另一状态5为下单状态）
                myEarnMoney += sellGoods.getG_price();
            }
        }
        mTvEarn.setText(String.valueOf(myEarnMoney));
        u_id = PreferenceManager.getDefaultSharedPreferences(this).getString("u_id", "");
        adapter = new MySellAdapter(this, sellGoodsList, u_id);
        adapter.setOnClick(new MySellAdapter.OnClick() {
            @Override
            public void onDeleteClick(String g_id) {
                handleDelete(g_id);
            }

            @Override
            public void onConfirmClick(double g_price) {
                myEarnMoney += g_price;
                mTvEarn.setText(String.valueOf(myEarnMoney));
            }

            @Override
            public void onRefuseClick(final SellGoods sellGoods) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("g_id", sellGoods.getG_id());
                    jsonObject.put("u_id", u_id);
                    jsonObject.put("buyer_id", sellGoods.getG_buyer_id());
                    jsonObject.put("g_name", sellGoods.getG_name());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpUtil.sendPostRequest(Constant.NEW_GOODS_ORDER_REFUSE_URL, jsonObject.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("拒绝商品订单", "请求失败，原因：" + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showMsg(MySellActivity.this, "拒绝订单失败", Toast.LENGTH_SHORT);
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
                                    Log.e("拒绝商品订单", "请求成功");
                                    ReleaseGoods releaseGoods = new ReleaseGoods(sellGoods.getG_id(), sellGoods.getG_name(),
                                            sellGoods.getG_desc(), sellGoods.getG_price(), sellGoods.getG_originalPrice(),
                                            sellGoods.getG_pic1(), sellGoods.getG_pic2(), sellGoods.getG_pic3(),
                                            0, sellGoods.getG_like(), sellGoods.getG_updateTime(), sellGoods.getG_t_id());
                                    releaseGoods.save();
                                    sellGoods.delete();
                                    adapter.removeGoods(sellGoods);
                                    adapter.notifyDataSetChanged();
                                    ToastUtil.showMsg(MySellActivity.this, "拒绝订单成功", Toast.LENGTH_SHORT);
                                } else {
                                    Log.e("拒绝商品订单", "请求失败");
                                    ToastUtil.showMsg(MySellActivity.this, "拒绝订单失败，请重试", Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    }
                });
            }
        });
        mRv.setAdapter(adapter);
    }

    private void initListener() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sellGoodsList = LitePal.findAll(SellGoods.class);
                adapter.setSellGoodsList(sellGoodsList);
                adapter.notifyDataSetChanged();
                mSwipeRefresh.setRefreshing(false);
                if (sellGoodsList.size() == 0) {
                    mLlNoGoods.setVisibility(View.VISIBLE);
                    mSwipeRefresh.setVisibility(View.GONE);
                } else {
                    mLlNoGoods.setVisibility(View.GONE);
                    mSwipeRefresh.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void handleDelete(final String g_id) {
        MessageDialog messageDialog = new MessageDialog(MySellActivity.this, R.style.MessageDialog);
        messageDialog.setTitle("提示").setContent("确认删除该宝贝？")
                .setCancel("删除", new MessageDialog.IOnCancelListener() {
                    @Override
                    public void onCancel(MessageDialog dialog) {
                        // 去服务器删除商品
                        deleteGoodsToServer(g_id);
                    }
                }).setConfirm("取消", new MessageDialog.IOnConfirmListener() {
            @Override
            public void onConfirm(MessageDialog dialog) {
                // do nothing
            }
        }).show();
    }

    private void deleteGoodsToServer(final String g_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("g_id", g_id);
            jsonObject.put("u_id", u_id);
            String json = jsonObject.toString();
            HttpUtil.sendPostRequest(Constant.REMOVE_GOODS_URL, json, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ToastUtil.showMsgOnCenter(MySellActivity.this, "删除失败", Toast.LENGTH_SHORT);
                    Log.e("MySellActivity", "删除商品失败:" + e.getMessage());
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
                                SellGoods sellGoods = LitePal.where("g_id=?", g_id).findFirst(SellGoods.class);
                                sellGoods.delete();
                                ReleaseGoods releaseGoods = LitePal.where("g_id=?", g_id).findFirst(ReleaseGoods.class);
                                releaseGoods.delete();
                                adapter.setSellGoodsList(
                                        LitePal.order("g_updateTime desc").find(SellGoods.class));
                                adapter.notifyDataSetChanged();
                                ToastUtil.showMsgOnCenter(MySellActivity.this, "删除成功", Toast.LENGTH_SHORT);
                                Log.e("MySellActivity", "删除商品成功");
                            } else {
                                ToastUtil.showMsgOnCenter(MySellActivity.this, "删除失败", Toast.LENGTH_SHORT);
                                Log.e("MySellActivity", "删除商品失败");
                            }
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
