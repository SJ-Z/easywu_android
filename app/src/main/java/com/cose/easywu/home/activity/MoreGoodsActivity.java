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
import com.cose.easywu.home.adapter.MoreGoodsAdapter;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;
import com.scwang.smartrefresh.header.FunGameHitBlockHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
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

public class MoreGoodsActivity extends BaseActivity {

    private TextView tvBack;
    private ImageView ivLoading;
    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private MoreGoodsAdapter adapter;
    private List<HomeDataBean.NewestInfoBean> goodsList = new ArrayList<>();
    private int pageCode = 0; // 当前加载的页数，初始为0

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_goods);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                pageCode = 0;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("pageCode", pageCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpUtil.sendPostRequest(Constant.NEWEST_GOODS_URL, jsonObject.toString(), new Callback() {
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
                        goodsList = JSON.parseArray(responseText, HomeDataBean.NewestInfoBean.class);
                        if (null == goodsList) {
                            return;
                        }
                        // 更新界面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setDatas(goodsList);
                                adapter.notifyDataSetChanged();
                                refreshlayout.finishRefresh(true);
                            }
                        });
                    }
                });
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshlayout) {
                pageCode++;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("pageCode", pageCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpUtil.sendPostRequest(Constant.NEWEST_GOODS_URL, jsonObject.toString(), new Callback() {
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
                                    ToastUtil.showMsg(MoreGoodsActivity.this, "没有更多数据了", Toast.LENGTH_SHORT);
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
        // 联网请求最新发布的数据
        getDataFromNet();
        // 初始化RecyclerView的数据
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MoreGoodsAdapter(this, goodsList);
        recyclerView.setAdapter(adapter);
    }

    private void getDataFromNet() {
        ivLoading.setVisibility(View.VISIBLE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pageCode", pageCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtil.sendPostRequest(Constant.NEWEST_GOODS_URL, jsonObject.toString(), new Callback() {
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
                goodsList = JSON.parseArray(responseText, HomeDataBean.NewestInfoBean.class);
                if (null == goodsList) {
                    return;
                }
                // 更新界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setDatas(goodsList);
                        adapter.notifyDataSetChanged();
                        ivLoading.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void initView() {
        tvBack = findViewById(R.id.tv_moregoods_back);
        ivLoading = findViewById(R.id.iv_moregoods_loading);
        Glide.with(this).load(R.drawable.gif_loading).apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)).into(ivLoading);
        refreshLayout = findViewById(R.id.refreshLayout_moregoods);
        recyclerView = findViewById(R.id.rv_moregoods);
        //设置 Header 为 贝塞尔雷达 样式
        refreshLayout.setRefreshHeader(new FunGameHitBlockHeader(this));
        //设置 Footer 为 球脉冲 样式
        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setEnableRefresh(true); // 是否启用下拉刷新功能，默认值为true
        refreshLayout.setEnableLoadMore(true); // 是否启用上拉加载功能，默认值为false
    }
}
