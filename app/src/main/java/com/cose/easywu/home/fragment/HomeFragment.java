package com.cose.easywu.home.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cose.easywu.R;
import com.cose.easywu.base.BaseFragment;
import com.cose.easywu.db.Type;
import com.cose.easywu.home.adapter.HomeFragmentAdapter;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.utils.Constant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.LitePal;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import okhttp3.Call;

public class HomeFragment extends BaseFragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private TextView mTvSearch;
    private RecyclerView mRv;
    private ImageButton mIbSearch, mIbTop;

    private HomeFragmentAdapter adapter;
    // 返回的数据
    HomeDataBean homeDataBean;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_home, null);
        mTvSearch = view.findViewById(R.id.tv_home_search);
        mRv = view.findViewById(R.id.rv_home);
        mIbSearch = view.findViewById(R.id.ib_home_search);
        mIbTop = view.findViewById(R.id.ib_home_top);

        // 设置点击事件
        initListener();
        return view;
    }

    @Override
    public void initData() {
        // 联网请求主页的数据
        getDataFromNet();
    }

    private void getDataFromNet() {
        OkHttpUtils
                .get()
                .url(Constant.HOME_URL)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "首页请求失败==" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "首页请求成功==" + response);
                        // 解析数据
                        try {
                            String responseText = URLDecoder.decode(response, "utf-8");
                            processData(responseText);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
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
                mRv.scrollToPosition(0);
            }
        });

        //搜素的监听
        mIbSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    // 页面定位到顶部
    public void scrollToTop() {
        mRv.scrollToPosition(0);
    }
}
