package com.cose.easywu.find.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.base.BaseFragment;
import com.cose.easywu.find.adapter.TypeGoodsAdapter;
import com.cose.easywu.find.bean.FindDataBean;
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

public class SearchFindPeopleFragment extends BaseFragment {

    private ImageView mIvLoading;
    private RefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private TypeGoodsAdapter adapter;
    private int pageCode = 0; // 当前加载的页数，初始为0
    private boolean isBottom = false; // 是否已加载完所有数据
    private List<FindDataBean.FindNewestInfo> mGoodsList = new ArrayList<>();
    private String key;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_type_find_goods, null);
        mIvLoading = view.findViewById(R.id.iv_type_goods_loading);
        Glide.with(this).load(R.drawable.gif_loading).apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)).into(mIvLoading);
        mRefreshLayout = view.findViewById(R.id.refreshLayout_type_goods);
        mRecyclerView = view.findViewById(R.id.rv_type_goods);
        //添加分割线
        mRecyclerView.addItemDecoration(new RecycleViewDivider(
                mContext, LinearLayoutManager.VERTICAL, 2,
                getResources().getColor(R.color.colorCut)));

        //设置 Header 为 贝塞尔雷达 样式
        mRefreshLayout.setRefreshHeader(new FunGameHitBlockHeader(mContext));
        //设置 Footer 为 球脉冲 样式
        mRefreshLayout.setRefreshFooter(new BallPulseFooter(mContext).setSpinnerStyle(SpinnerStyle.Scale));
        mRefreshLayout.setEnableRefresh(true); // 是否启用下拉刷新功能，默认值为true
        mRefreshLayout.setEnableLoadMore(true); // 是否启用上拉加载功能，默认值为false

        return view;
    }

    @Override
    public void initData() {
        key = getArguments().getString("key");
        // 联网请求最新发布的数据
        getDataFromNet();
        // 初始化RecyclerView的数据
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new TypeGoodsAdapter(mContext, mGoodsList, false);
        mRecyclerView.setAdapter(adapter);

        initListener();
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                pageCode = 0;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("pageCode", pageCode);
                    jsonObject.put("key", key);
                    jsonObject.put("isFindGoods", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpUtil.sendPostRequest(Constant.SEARCH_FIND_GOODS_URL, jsonObject.toString(), new Callback() {
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
                        mGoodsList = JSON.parseArray(responseText, FindDataBean.FindNewestInfo.class);
                        if (null == mGoodsList) {
                            return;
                        }
                        // 更新界面
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setDatas(mGoodsList);
                                    adapter.notifyDataSetChanged();
                                    refreshlayout.finishRefresh(true);
                                }
                            });
                        }
                    }
                });
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshlayout) {
                if (isBottom) {
                    ToastUtil.showMsg(mContext, "没有更多数据了", Toast.LENGTH_SHORT);
                    return;
                }
                pageCode++;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("pageCode", pageCode);
                    jsonObject.put("key", key);
                    jsonObject.put("isFindGoods", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpUtil.sendPostRequest(Constant.SEARCH_FIND_GOODS_URL, jsonObject.toString(), new Callback() {
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
                        final List<FindDataBean.FindNewestInfo> moregoodsList = JSON.parseArray(responseText, FindDataBean.FindNewestInfo.class);
                        if (null == moregoodsList) {
                            return;
                        }
                        // 更新界面
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (moregoodsList.size() == 0) {
                                        isBottom = true;
                                        ToastUtil.showMsg(mContext, "没有更多数据了", Toast.LENGTH_SHORT);
                                    } else {
                                        adapter.addDatas(moregoodsList);
                                        adapter.notifyDataSetChanged();
                                    }
                                    refreshlayout.finishLoadMore(true);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void getDataFromNet() {
        mIvLoading.setVisibility(View.VISIBLE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pageCode", pageCode);
            jsonObject.put("key", key);
            jsonObject.put("isFindGoods", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtil.sendPostRequest(Constant.SEARCH_FIND_GOODS_URL, jsonObject.toString(), new Callback() {
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
                mGoodsList = JSON.parseArray(responseText, FindDataBean.FindNewestInfo.class);
                if (null == mGoodsList) {
                    return;
                }
                // 更新界面
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setDatas(mGoodsList);
                            adapter.notifyDataSetChanged();
                            mIvLoading.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }
}
