package com.cose.easywu.home.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.cose.easywu.R;
import com.cose.easywu.app.LoginActivity;
import com.cose.easywu.app.MainActivity;
import com.cose.easywu.base.BaseFragment;
import com.cose.easywu.db.Goods;
import com.cose.easywu.db.Type;
import com.cose.easywu.gson.msg.LoginMsg;
import com.cose.easywu.home.adapter.HomeFragmentAdapter;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.LitePal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends BaseFragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private TextView mTvSearch;
    private RecyclerView mRv;
    private ImageButton mIbSearch, mIbTop;
    private ScrollView mSv;
    private LinearLayout mLlConnect;
    private Button mBtnConnect;

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
        mTvSearch = view.findViewById(R.id.tv_home_search);
        mRv = view.findViewById(R.id.rv_home);
        mIbSearch = view.findViewById(R.id.ib_home_search);
        mIbTop = view.findViewById(R.id.ib_home_top);
        mSv = view.findViewById(R.id.sv_home);
        mLlConnect = view.findViewById(R.id.ll_home_disconnect);
        mBtnConnect = view.findViewById(R.id.btn_home_connect);

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
        String u_id = pref.getString("u_id", "");
        String json = new Gson().toJson(u_id);
        String address = Constant.HOME_URL;

        HttpUtil.sendPostRequest(address, json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
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
            // 设置适配器
            adapter = new HomeFragmentAdapter(mContext, homeDataBean);
            mRv.setAdapter(adapter);
            // 设置布局管理器
            GridLayoutManager manager = new GridLayoutManager(mContext, 1);
            // 设置跨度大小的监听
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position <= 2) {
                        // 隐藏回到顶部按钮
                        mIbTop.setVisibility(View.GONE);
                    } else {
                        // 显示回到顶部按钮
                        mIbTop.setVisibility(View.VISIBLE);
                    }
                    // 只能返回1
                    return 1;
                }
            });
            mRv.setLayoutManager(manager);

            // 将Type数据存储到本地数据库
            LitePal.deleteAll(Type.class);
            for (HomeDataBean.TypeInfoBean typeInfoBean : homeDataBean.getType_info()) {
                Type type = new Type(typeInfoBean.getT_id(), typeInfoBean.getT_name(), typeInfoBean.getT_pic());
                type.save();
            }
            // 将用户收藏的商品数据存储到本地数据库
            LitePal.deleteAll(Goods.class);
            for (Goods goods : homeDataBean.getGoodsLikeList()) {
                goods.save();
            }
        } else {
            // 无数据
            mSv.setVisibility(View.GONE);
            mLlConnect.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {
        //置顶的监听
        mIbTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 回到顶部
                mRv.scrollToPosition(0);
            }
        });

        //搜素的监听
        mIbSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 重试按钮的监听
        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromNet();
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
