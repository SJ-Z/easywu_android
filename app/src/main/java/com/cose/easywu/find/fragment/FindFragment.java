package com.cose.easywu.find.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.base.BaseFragment;
import com.cose.easywu.db.BuyGoods;
import com.cose.easywu.db.FindType;
import com.cose.easywu.db.LikeFindGoods;
import com.cose.easywu.db.LikeGoods;
import com.cose.easywu.db.ReleaseFindGoods;
import com.cose.easywu.db.ReleaseGoods;
import com.cose.easywu.db.SellGoods;
import com.cose.easywu.db.Type;
import com.cose.easywu.find.adapter.FindFragmentAdapter;
import com.cose.easywu.find.bean.FindDataBean;
import com.cose.easywu.home.adapter.HomeFragmentAdapter;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.home.fragment.HomeFragment;
import com.cose.easywu.utils.CacheUtils;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;

import org.litepal.LitePal;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FindFragment extends BaseFragment {

    private static final String TAG = FindFragment.class.getSimpleName();

    private RecyclerView mRv;
    private LinearLayout mLlConnect;
    private Button mBtnConnect;
    private ImageView mIvLoading;

    private SharedPreferences pref;
    private FindFragmentAdapter adapter;
    // 返回的数据
    private FindDataBean findDataBean;

    private BroadcastReceiver receiver;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_find, null);
        mRv = view.findViewById(R.id.rv_find);
        mLlConnect = view.findViewById(R.id.ll_find_disconnect);
        mBtnConnect = view.findViewById(R.id.btn_find_connect);
        mIvLoading = view.findViewById(R.id.iv_find_loading);
        Glide.with(mContext).load(R.drawable.gif_loading).apply(new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)).into(mIvLoading);

        // 设置布局管理器
        final GridLayoutManager manager = new GridLayoutManager(mContext, 1);
        mRv.setLayoutManager(manager);
        // 设置点击事件
        initListener();

        // 注册广播监听器
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.RELEASE_NEW_RELEASE);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                adapter.requestNewestGoods();
            }
        };
        localBroadcastManager.registerReceiver(receiver, intentFilter);
        return view;
    }

    @Override
    public void initData() {
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        // 联网请求失物招领的数据
        getDataFromNet();
    }

    private void getDataFromNet() {
        mIvLoading.setVisibility(View.VISIBLE);
        String u_id = pref.getString("u_id", "");
        String json = "{'u_id':'" + u_id + "'}";
        String address = Constant.HOME_FIND_URL;

        HttpUtil.sendPostRequest(address, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mIvLoading.setVisibility(View.GONE);
                            mRv.setVisibility(View.GONE);
                            mLlConnect.setVisibility(View.VISIBLE);
                        }
                    });
                }
                Log.e(TAG, "失物招领首页请求失败==" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null == response.body()) {
                    return;
                }
                // 解析数据
                String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                processData(responseText);
                Log.d(TAG, "失物招领首页请求成功==" + response);
            }
        });
    }

    private void processData(String json) {
        findDataBean = JSON.parseObject(json, FindDataBean.class);
        if (findDataBean != null) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIvLoading.setVisibility(View.GONE);
                        mRv.setVisibility(View.VISIBLE);
                        mLlConnect.setVisibility(View.GONE);
                        // 设置适配器
                        adapter = new FindFragmentAdapter(mContext, findDataBean);
                        mRv.setAdapter(adapter);

                        // 将FindType数据存储到本地数据库
                        LitePal.deleteAll(FindType.class);
                        LitePal.saveAll(findDataBean.getFindTypeList());

                        // 将用户收藏的失物招领信息存储到本地数据库
                        LitePal.deleteAll(LikeFindGoods.class);
                        for (LikeFindGoods goods : findDataBean.getLikeFindGoodsList()) {
                            goods.save();
                        }

                        // 将“我发布的”失物招领信息存储到本地数据库
                        LitePal.deleteAll(ReleaseFindGoods.class);
                        LitePal.saveAll(findDataBean.getReleaseFindGoodsList());
                    }
                });
            }
        } else {
            // 无数据
        }
    }

    private void initListener() {
        // 重试按钮的监听
        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromNet();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CacheUtils.clearImageAllCache(mContext);
        Glide.get(mContext).clearMemory();
        if (receiver != null) {
            localBroadcastManager.unregisterReceiver(receiver);
            receiver = null;
        }
    }
}
