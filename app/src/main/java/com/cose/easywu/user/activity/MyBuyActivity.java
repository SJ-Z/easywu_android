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
import com.cose.easywu.db.BuyGoods;
import com.cose.easywu.db.ReleaseGoods;
import com.cose.easywu.db.SellGoods;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.user.adapter.MyBuyAdapter;
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

public class MyBuyActivity extends BaseActivity {

    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRv;
    private LinearLayout mLlNoGoods;
    private ImageView mIvBack;
    private TextView mTvCost;

    private MyBuyAdapter adapter;
    private List<BuyGoods> buyGoodsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_buy);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mSwipeRefresh = findViewById(R.id.swipe_refresh_mybuy);
        mRv = findViewById(R.id.rv_mybuy);
        mLlNoGoods = findViewById(R.id.ll_mybuy_nogoods);
        mIvBack = findViewById(R.id.iv_mybuy_back);
        mTvCost = findViewById(R.id.tv_mybuy_cost);

        if (LitePal.findAll(BuyGoods.class).size() == 0) {
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
        buyGoodsList = LitePal.order("g_updateTime desc").find(BuyGoods.class);
        // 更新“在简物消费了xx元”的价格
        double myCostMoney = 0.0;
        for (BuyGoods buyGoods : buyGoodsList) {
            if (buyGoods.getG_state() == 1) { // 1状态为已卖出（另一状态5为下单状态）
                myCostMoney += buyGoods.getG_price();
            }
        }
        mTvCost.setText(String.valueOf(myCostMoney));
        String u_id = PreferenceManager.getDefaultSharedPreferences(this).getString("u_id", "");
        adapter = new MyBuyAdapter(this, buyGoodsList, u_id);
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
                buyGoodsList = LitePal.findAll(BuyGoods.class);
                adapter.setBuyGoodsList(buyGoodsList);
                adapter.notifyDataSetChanged();
                mSwipeRefresh.setRefreshing(false);
                if (buyGoodsList.size() == 0) {
                    mLlNoGoods.setVisibility(View.VISIBLE);
                    mSwipeRefresh.setVisibility(View.GONE);
                } else {
                    mLlNoGoods.setVisibility(View.GONE);
                    mSwipeRefresh.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
