package com.cose.easywu.user.fragment.myrelease;

import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseFragment;
import com.cose.easywu.db.LikeGoods;
import com.cose.easywu.db.ReleaseGoods;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.user.adapter.MyLikeGoodsAdapter;
import com.cose.easywu.user.adapter.MyReleaseGoodsAdapter;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.DateUtil;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;
import com.cose.easywu.widget.MessageDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyReleaseGoodsFragment extends BaseFragment {

    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRv;
    private LinearLayout mLlNoGoods;
    private MyReleaseGoodsAdapter adapter;
    private String u_id;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_myrelease_goods, null);
        mSwipeRefresh = view.findViewById(R.id.swipe_refresh_myreleasegoods);
        mSwipeRefresh.setColorSchemeResources(R.color.colorLoginButton);
        mRv = view.findViewById(R.id.rv_myreleasegoods);
        mLlNoGoods = view.findViewById(R.id.ll_myreleasegoods_nogoods);
        if (LitePal.findAll(ReleaseGoods.class).size() == 0) {
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
        adapter = new MyReleaseGoodsAdapter(mContext);
        adapter.setOnPolishClick(new MyReleaseGoodsAdapter.OnPolishClick() {
            @Override
            public void click(View view, TextView tvUpdateTime, ReleaseGoods goods) {
                setPolishToServer(tvUpdateTime, goods, (TextView) view);
            }
        });
        adapter.setOnDeleteClick(new MyReleaseGoodsAdapter.OnDeleteClick() {
            @Override
            public void click(ReleaseGoods goods) {
                handleDelete(goods);
            }
        });
        mRv.setAdapter(adapter);
    }

    private void initListener() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.setReleaseGoodsList(LitePal.findAll(ReleaseGoods.class));
                adapter.notifyDataSetChanged();
                mSwipeRefresh.setRefreshing(false);
                if (LitePal.findAll(ReleaseGoods.class).size() == 0) {
                    mLlNoGoods.setVisibility(View.VISIBLE);
                    mSwipeRefresh.setVisibility(View.GONE);
                } else {
                    mLlNoGoods.setVisibility(View.GONE);
                    mSwipeRefresh.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void handleDelete(final ReleaseGoods goods) {
        MessageDialog messageDialog = new MessageDialog(mContext, R.style.MessageDialog);
        messageDialog.setTitle("提示").setContent("确认删除该宝贝？")
                .setCancel("删除", new MessageDialog.IOnCancelListener() {
                    @Override
                    public void onCancel(MessageDialog dialog) {
                        // 去服务器删除商品
                        deleteGoodsToServer(goods);
                    }
                }).setConfirm("取消", new MessageDialog.IOnConfirmListener() {
            @Override
            public void onConfirm(MessageDialog dialog) {
                // do nothing
            }
        }).show();
    }

    private void deleteGoodsToServer(final ReleaseGoods goods) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("g_id", goods.getG_id());
            jsonObject.put("u_id", u_id);
            String json = jsonObject.toString();
            HttpUtil.sendPostRequest(Constant.DELETE_GOODS_URL, json, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ToastUtil.showMsgOnCenter(mContext, "删除失败", Toast.LENGTH_SHORT);
                    Log.e("MyReleaseGoodsFragment", "删除商品失败:" + e.getMessage());
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
                                if (msg.getCode().equals("1")) {
                                    goods.delete();
                                    adapter.setReleaseGoodsList(LitePal.findAll(ReleaseGoods.class));
                                    adapter.notifyDataSetChanged();
                                    ToastUtil.showMsgOnCenter(mContext, "删除成功", Toast.LENGTH_SHORT);
                                    Log.e("MyReleaseGoodsFragment", "删除商品成功");
                                } else {
                                    ToastUtil.showMsgOnCenter(mContext, "删除失败", Toast.LENGTH_SHORT);
                                    Log.e("MyReleaseGoodsFragment", "删除商品失败");
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

    private void setPolishToServer(final TextView tvUpdateTime, final ReleaseGoods goods, final TextView tvPolish) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("g_id", goods.getG_id());
            jsonObject.put("u_id", u_id);
            String json = jsonObject.toString();
            HttpUtil.sendPostRequest(Constant.POLISH_GOODS_URL, json, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ToastUtil.showMsgOnCenter(mContext, "擦亮失败", Toast.LENGTH_SHORT);
                    Log.e("MyReleaseGoodsFragment", "擦亮失败:" + e.getMessage());
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
                                if (msg.getCode().equals("1")) {
                                    goods.setG_updateTime(new Date(Long.valueOf(msg.getMsg())));
                                    goods.save();
                                    tvUpdateTime.setText(DateUtil.getDatePoor(goods.getG_updateTime(), new Date()) + "擦亮");
                                    tvPolish.setText("已擦亮");
                                    tvPolish.setTextColor(mContext.getResources().getColor(R.color.colorHint));
                                    ToastUtil.showMsgOnCenter(mContext, "擦亮成功", Toast.LENGTH_SHORT);
                                    Log.e("MyReleaseGoodsFragment", "擦亮成功");
                                } else {
                                    ToastUtil.showMsgOnCenter(mContext, "擦亮失败", Toast.LENGTH_SHORT);
                                    Log.e("MyReleaseGoodsFragment", "擦亮失败");
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

}
