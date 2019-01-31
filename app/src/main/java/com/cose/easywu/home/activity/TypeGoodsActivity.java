package com.cose.easywu.home.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.home.adapter.GoodsAdapter;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.RecycleViewDivider;
import com.cose.easywu.utils.ToastUtil;
import com.scwang.smartrefresh.header.FunGameHitBlockHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TypeGoodsActivity extends BaseActivity {

    private TextView mTvBack, mTvTypeName;
    private ImageView mIvLoading;
    private RefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private GoodsAdapter adapter;
    private int pageCode = 0; // 当前加载的页数，初始为0
    private boolean isBottom = false; // 是否已加载完所有数据
    private String type_id, type_name;
    private List<HomeDataBean.NewestInfoBean> mGoodsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_goods);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                pageCode = 0;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("pageCode", pageCode);
                    jsonObject.put("type_id", type_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpUtil.sendPostRequest(Constant.TYPE_GOODS_URL, jsonObject.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        refreshlayout.finishRefresh(false);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (null == response.body()) {
                            return;
                        }

                        String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                        // 解析数据
                        mGoodsList = JSON.parseArray(responseText, HomeDataBean.NewestInfoBean.class);
                        if (null == mGoodsList) {
                            return;
                        }
                        // 更新界面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setDatas(mGoodsList);
                                adapter.notifyDataSetChanged();
                                refreshlayout.finishRefresh(true);
                            }
                        });
                    }
                });
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshlayout) {
                if (isBottom) {
                    ToastUtil.showMsg(TypeGoodsActivity.this, "没有更多数据了", Toast.LENGTH_SHORT);
                    return;
                }
                pageCode++;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("pageCode", pageCode);
                    jsonObject.put("type_id", type_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpUtil.sendPostRequest(Constant.TYPE_GOODS_URL, jsonObject.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        refreshlayout.finishLoadMore(false);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (null == response.body()) {
                            return;
                        }

                        String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                        // 解析数据
                        final List<HomeDataBean.NewestInfoBean> moregoodsList = JSON.parseArray(responseText, HomeDataBean.NewestInfoBean.class);
                        if (null == moregoodsList) {
                            return;
                        }
                        // 更新界面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (moregoodsList.size() == 0) {
                                    isBottom = true;
                                    ToastUtil.showMsg(TypeGoodsActivity.this, "没有更多数据了", Toast.LENGTH_SHORT);
                                } else {
                                    adapter.addDatas(moregoodsList);
                                    adapter.notifyDataSetChanged();
                                }
                                refreshlayout.finishLoadMore(true);
                            }
                        });
                    }
                });
            }
        });
    }

    private void initData() {
        type_id = getIntent().getStringExtra("type_id");
        type_name = getIntent().getStringExtra("type_name");
        mTvTypeName.setText(type_name);
        // 联网请求最新发布的数据
        getDataFromNet();
        // 初始化RecyclerView的数据
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new GoodsAdapter(this, mGoodsList);
        mRecyclerView.setAdapter(adapter);
    }

    private void getDataFromNet() {
        mIvLoading.setVisibility(View.VISIBLE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pageCode", pageCode);
            jsonObject.put("type_id", type_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtil.sendPostRequest(Constant.TYPE_GOODS_URL, jsonObject.toString(), new Callback() {
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
                mGoodsList = JSON.parseArray(responseText, HomeDataBean.NewestInfoBean.class);
                if (null == mGoodsList) {
                    return;
                }
                // 更新界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setDatas(mGoodsList);
                        adapter.notifyDataSetChanged();
                        mIvLoading.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void initView() {
        mTvBack = findViewById(R.id.tv_type_goods_back);
        mIvLoading = findViewById(R.id.iv_type_goods_loading);
        Glide.with(this).load(R.drawable.gif_loading).apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)).into(mIvLoading);
        mTvTypeName = findViewById(R.id.tv_type_goods_type_name);
        mRefreshLayout = findViewById(R.id.refreshLayout_type_goods);
        mRecyclerView = findViewById(R.id.rv_type_goods);
        //添加分割线
        mRecyclerView.addItemDecoration(new RecycleViewDivider(
                this, LinearLayoutManager.VERTICAL, 2,
                getResources().getColor(R.color.colorCut)));

        //设置 Header 为 贝塞尔雷达 样式
        mRefreshLayout.setRefreshHeader(new FunGameHitBlockHeader(this));
        //设置 Footer 为 球脉冲 样式
        mRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshLayout.setEnableRefresh(true); // 是否启用下拉刷新功能，默认值为true
        mRefreshLayout.setEnableLoadMore(true); // 是否启用上拉加载功能，默认值为false
    }

}
