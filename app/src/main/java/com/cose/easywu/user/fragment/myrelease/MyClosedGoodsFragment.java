package com.cose.easywu.user.fragment.myrelease;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseFragment;
import com.cose.easywu.db.ReleaseGoods;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.release.activity.ReleaseActivity;
import com.cose.easywu.user.adapter.MyClosedGoodsAdapter;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ImageUtils;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;
import com.cose.easywu.widget.MessageDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyClosedGoodsFragment extends BaseFragment {

    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRv;
    private LinearLayout mLlNoGoods;
    private static ProgressDialog progressDialog;
    private MyClosedGoodsAdapter adapter;
    private String u_id;
    private SharedPreferences.Editor editor;

    private MyClosedGoodsFragment.MyHandler mHandler = new MyClosedGoodsFragment.MyHandler(this);
    private static int MSG_PICLEN = 0;
    private static int MSG_PIC = 1;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_myclosed_goods, null);
        mSwipeRefresh = view.findViewById(R.id.swipe_refresh_myclosedgoods);
        mSwipeRefresh.setColorSchemeResources(R.color.colorLoginButton);
        mRv = view.findViewById(R.id.rv_myclosedgoods);
        mLlNoGoods = view.findViewById(R.id.ll_myclosedgoods_nogoods);
        if (LitePal.where("g_state!=?", "0").find(ReleaseGoods.class).size() == 0) {
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
        editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        adapter = new MyClosedGoodsAdapter(mContext);
        adapter.setOnReleaseClick(new MyClosedGoodsAdapter.OnReleaseClick() {
            @Override
            public void click(ReleaseGoods goods) {
                handleRelease(goods);
            }
        });
        adapter.setOnDeleteClick(new MyClosedGoodsAdapter.OnDeleteClick() {
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
                adapter.setClosedGoodsList(LitePal.where("g_state!=?", "0")
                        .order("g_updateTime desc").find(ReleaseGoods.class));
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

    private void handleRelease(ReleaseGoods goods) {
        showProgressDialog();
        editor.putBoolean(u_id + "_hasSaveContent", true);
        editor.putString(u_id + "_name", goods.getG_name());
        editor.putString(u_id + "_desc", goods.getG_desc());
        editor.putString(u_id + "_price", String.valueOf(goods.getG_price()));
        editor.putString(u_id + "_originalPrice", String.valueOf(goods.getG_originalPrice()));
        editor.putString(u_id + "_typeId", goods.getG_t_id());
        editor.putString("g_id", goods.getG_id());

        String[] picAddr = {goods.getG_pic1(), goods.getG_pic2(), goods.getG_pic3()};
        loadPicToCache(picAddr);
        editor.apply();
    }

    private void loadPicToCache(final String[] picAddr) {
        int picLen = 1;
        if (picAddr[1] != null) {
            picLen++;
            if (picAddr[2] != null) {
                picLen++;
            }
        }

        editor.putInt(u_id + "_picLen", picLen);
        // 发送消息
        Message message = new Message();
        message.what = MSG_PICLEN;
        message.arg1 = picLen;
        mHandler.sendMessage(message);

        OkHttpClient client = new OkHttpClient();
        for (int i = 0; i < picLen; i++) {
            final int index = i;
            final Request request = new Request.Builder().get()
                    .url(Constant.BASE_PIC_URL + picAddr[i])
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            byte[] bytes = response.body().bytes();
                            final Bitmap bitmap = ImageUtils.getBitmapFromByte(bytes, 100, 100);
                            String picName = u_id + "_pic" + index;
                            editor.putString(picName, ImageUtils.savePhotoToCache(bitmap, picName));
                            editor.apply();

                            // 发送消息
                            Message message = new Message();
                            message.what = MSG_PIC;
                            mHandler.sendMessage(message);
                        }
                    }
                }
            });
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("正在准备界面...");
        }
        progressDialog.show();
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
            HttpUtil.sendPostRequest(Constant.REMOVE_GOODS_URL, json, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ToastUtil.showMsgOnCenter(mContext, "删除失败", Toast.LENGTH_SHORT);
                    Log.e("MyClosedGoodsFragment", "删除商品失败:" + e.getMessage());
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
                                    adapter.setClosedGoodsList(
                                            LitePal.where("g_state!=?", "0")
                                            .order("g_updateTime desc").find(ReleaseGoods.class));
                                    adapter.notifyDataSetChanged();
                                    ToastUtil.showMsgOnCenter(mContext, "删除成功", Toast.LENGTH_SHORT);
                                    Log.e("MyClosedGoodsFragment", "删除商品成功");
                                } else {
                                    ToastUtil.showMsgOnCenter(mContext, "删除失败", Toast.LENGTH_SHORT);
                                    Log.e("MyClosedGoodsFragment", "删除商品失败");
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

    private static class MyHandler extends Handler {
        private WeakReference<Fragment> fragmentWeakReference;
        private int picLen;
        private int count = 0;

        public MyHandler(Fragment fragment) {
            fragmentWeakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            Fragment fragment = fragmentWeakReference.get();
            if (fragment != null) {
                if (msg.what == MSG_PICLEN) {
                    picLen = msg.arg1;
                } else if (msg.what == MSG_PIC) {
                    count++;
                    if (count == picLen) {
                        count = 0; // 为下一次接收请求做准备
                        progressDialog.dismiss();
                        fragment.startActivity(new Intent(fragment.getContext(), ReleaseActivity.class));
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
