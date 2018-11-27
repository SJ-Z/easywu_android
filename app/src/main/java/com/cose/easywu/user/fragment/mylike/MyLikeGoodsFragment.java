package com.cose.easywu.user.fragment.mylike;

import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseFragment;
import com.cose.easywu.db.LikeGoods;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.user.adapter.MyLikeGoodsAdapter;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyLikeGoodsFragment extends BaseFragment {

    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRv;
    private LinearLayout mLlNoGoods;
    private MyLikeGoodsAdapter adapter;
    private String u_id;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_mylike_goods, null);
        mSwipeRefresh = view.findViewById(R.id.swipe_refresh_mylikegoods);
        mSwipeRefresh.setColorSchemeResources(R.color.colorLoginButton);
        mRv = view.findViewById(R.id.rv_mylikegoods);
        mLlNoGoods = view.findViewById(R.id.ll_mylikegoods_nogoods);
        if (LitePal.findAll(LikeGoods.class).size() == 0) {
            mLlNoGoods.setVisibility(View.VISIBLE);
            mSwipeRefresh.setVisibility(View.GONE);
        } else {
            // 设置布局管理器
            LinearLayoutManager manager = new LinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            mRv.setLayoutManager(manager);
            initListener();
        }
        return view;
    }

    @Override
    public void initData() {
        u_id = PreferenceManager.getDefaultSharedPreferences(mContext).getString("u_id", "");
        adapter = new MyLikeGoodsAdapter(mContext);
        adapter.setOnLikeClick(new MyLikeGoodsAdapter.OnLikeClick() {
            @Override
            public void click(View view, LinearLayout linearLayout, boolean like, String g_id) {
                view.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                setLikeFromServer(g_id, like);
            }
        });
        mRv.setAdapter(adapter);
    }

    private void initListener() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                removeFromLitePal();
                adapter.setLikeGoodsList(LitePal.findAll(LikeGoods.class));
                adapter.notifyDataSetChanged();
                mSwipeRefresh.setRefreshing(false);
                if (LitePal.findAll(LikeGoods.class).size() == 0) {
                    mLlNoGoods.setVisibility(View.VISIBLE);
                    mSwipeRefresh.setVisibility(View.GONE);
                } else {
                    mLlNoGoods.setVisibility(View.GONE);
                    mSwipeRefresh.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setLikeFromServer(String g_id, final boolean like) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("g_id", g_id);
            jsonObject.put("u_id", u_id);
            jsonObject.put("like", like);
            String json = jsonObject.toString();
            HttpUtil.sendPostRequest(Constant.SET_LIKE_GOODS_URL, json, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

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
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (like) {
                                    ToastUtil.showImageToast(mContext, msg.getMsg(),
                                            R.drawable.ic_goods_like_press, Toast.LENGTH_SHORT);
                                } else {
                                    ToastUtil.showMsgOnCenter(mContext, msg.getMsg(),
                                            Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeFromLitePal() {
        List<LikeGoods> removeGoodsList = adapter.getRemoveGoodsList();
        if (removeGoodsList != null && removeGoodsList.size() > 0) {
            for (LikeGoods goods : removeGoodsList) {
                goods.delete();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeFromLitePal();
    }
}
