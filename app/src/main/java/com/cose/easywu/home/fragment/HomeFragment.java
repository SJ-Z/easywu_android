package com.cose.easywu.home.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cose.easywu.R;
import com.cose.easywu.base.BaseFragment;
import com.cose.easywu.db.LikeGoods;
import com.cose.easywu.db.Type;
import com.cose.easywu.home.adapter.HomeFragmentAdapter;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;

import org.litepal.LitePal;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends BaseFragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private RecyclerView mRv;
    private ImageButton mIbTop;
    private LinearLayout mLlConnect;
    private Button mBtnConnect;
    private ProgressBar mPb;

    private SharedPreferences pref;
    private HomeFragmentAdapter adapter;
    // 返回的数据
    HomeDataBean homeDataBean;

    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_home, null);
        mRv = view.findViewById(R.id.rv_home);
        mIbTop = view.findViewById(R.id.ib_home_top);
        mLlConnect = view.findViewById(R.id.ll_home_disconnect);
        mBtnConnect = view.findViewById(R.id.btn_home_connect);
        mPb = view.findViewById(R.id.pb_home);

        // 设置布局管理器
        final GridLayoutManager manager = new GridLayoutManager(mContext, 1);
        mRv.setLayoutManager(manager);
        // 设置点击事件
        initListener();

        // 注册广播监听器
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        intentFilter = new IntentFilter();
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
        // 联网请求主页的数据
        getDataFromNet();
    }

    private void getDataFromNet() {
        mPb.setVisibility(View.VISIBLE);
        String u_id = pref.getString("u_id", "");
        String json = "{'u_id':'" + u_id + "'}";
        String address = Constant.HOME_URL;

        HttpUtil.sendPostRequest(address, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPb.setVisibility(View.GONE);
                            mRv.setVisibility(View.GONE);
                            mLlConnect.setVisibility(View.VISIBLE);
                        }
                    });
                }
                Log.e(TAG, "首页请求失败==" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null == response.body()) {
                    return;
                }
                // 解析数据
                String responseText = URLDecoder.decode(response.body().string(), "utf-8");
                processData(responseText);
                Log.d(TAG, "首页请求成功==" + response);
            }
        });
    }

    private void processData(String json) {
        homeDataBean = JSON.parseObject(json, HomeDataBean.class);
        if (homeDataBean != null) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPb.setVisibility(View.GONE);
                        mRv.setVisibility(View.VISIBLE);
                        mLlConnect.setVisibility(View.GONE);
                        // 设置适配器
                        adapter = new HomeFragmentAdapter(mContext, homeDataBean);
                        mRv.setAdapter(adapter);

                        // 将Type数据存储到本地数据库
                        LitePal.deleteAll(Type.class);
                        for (HomeDataBean.TypeInfoBean typeInfoBean : homeDataBean.getType_info()) {
                            Type type = new Type(typeInfoBean.getT_id(), typeInfoBean.getT_name(), typeInfoBean.getT_pic());
                            type.save();
                        }
                        // 将用户收藏的商品数据存储到本地数据库
                        LitePal.deleteAll(LikeGoods.class);
                        for (LikeGoods goods : homeDataBean.getGoodsLikeList()) {
                            goods.save();
                        }
                    }
                });
            }
        } else {
            // 无数据
        }
    }

    private void initListener() {
        //置顶的监听
        mIbTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 回到顶部
//                mRv.scrollToPosition(0);
                mRv.smoothScrollToPosition(0);
            }
        });

        // 重试按钮的监听
        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromNet();
            }
        });

        // RecyclerView滚动的监听
        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mRv.getLayoutManager() != null) {
                    int pos = ((GridLayoutManager) mRv.getLayoutManager()).findFirstVisibleItemPosition();
                    if (pos > 2) {
                        mIbTop.setVisibility(View.VISIBLE);
                    } else {
                        mIbTop.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    // 页面定位到顶部
    public void scrollToTop() {
        mRv.scrollToPosition(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            localBroadcastManager.unregisterReceiver(receiver);
            receiver = null;
        }
    }
}
