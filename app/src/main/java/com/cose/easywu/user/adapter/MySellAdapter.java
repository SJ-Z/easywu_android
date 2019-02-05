package com.cose.easywu.user.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.db.ReleaseGoods;
import com.cose.easywu.db.SellGoods;
import com.cose.easywu.db.User;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.home.activity.GoodsInfoActivity;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.message.activity.ChatActivity;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;
import com.hyphenate.easeui.EaseConstant;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MySellAdapter extends RecyclerView.Adapter<MySellAdapter.ViewHolder> {

    private Context mContext;
    private List<SellGoods> sellGoodsList;
    private OnClick onClick;
    private User user;

    public static final String GOODS_BEAN = "goodsBean";

    public MySellAdapter(Context context, List<SellGoods> sellGoodsList, String u_id) {
        mContext = context;
        this.sellGoodsList = sellGoodsList;
        user = LitePal.where("u_id=?", u_id).findFirst(User.class);
    }

    @NonNull
    @Override
    public MySellAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mysellgoods, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MySellAdapter.ViewHolder holder, final int position) {
        final SellGoods goods = sellGoodsList.get(position);
        holder.tvPrice.setText(String.valueOf(goods.getG_price()));
        holder.tvGoodsName.setText(goods.getG_name());
        Glide.with(mContext).load(Constant.BASE_PIC_URL + goods.getG_pic1()).apply(new RequestOptions()
                .placeholder(R.drawable.ic_loading_pic).error(R.drawable.ic_error_goods)).into(holder.ivPic);
        if (goods.getG_state() == 1) {
            holder.tvMsg.setText("交易成功");
            holder.tvMsg.setTextColor(Color.GREEN);
            holder.tvDelete.setVisibility(View.VISIBLE);
            holder.tvConfirm.setVisibility(View.INVISIBLE);
            holder.tvRefuse.setVisibility(View.INVISIBLE);
        } else {
            holder.tvMsg.setText("等待卖家确认");
            holder.tvMsg.setTextColor(Color.RED);
            holder.tvDelete.setVisibility(View.INVISIBLE);
            holder.tvConfirm.setVisibility(View.VISIBLE);
            holder.tvRefuse.setVisibility(View.VISIBLE);
        }
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动商品详情页面
                startGoodsInfoActivity(goods);
            }
        });
        holder.tvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                // 传递参数，会话id即环信id
                intent.putExtra(EaseConstant.EXTRA_USER_ID, goods.getG_buyer_id());
                // 传递商品信息
                intent.putExtra("isGoods", true);
                intent.putExtra("goods_id", goods.getG_id());
                intent.putExtra("goods_name", goods.getG_name());
                intent.putExtra("goods_pic", Constant.BASE_PIC_URL + goods.getG_pic1());
                intent.putExtra("goods_price", goods.getG_price());
                mContext.startActivity(intent);
            }
        });
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick != null) {
                    onClick.onDeleteClick(goods.getG_id());
                }
            }
        });
        holder.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("g_id", goods.getG_id());
                    jsonObject.put("u_id", user.getU_id());
                    jsonObject.put("buyer_id", goods.getG_buyer_id());
                    jsonObject.put("g_name", goods.getG_name());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpUtil.sendPostRequest(Constant.NEW_GOODS_ORDER_CONFIRM_URL, jsonObject.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("确定商品订单", "请求失败，原因：" + e.getMessage());
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showMsg(mContext, "确认订单失败", Toast.LENGTH_SHORT);
                            }
                        });
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

                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (msg.getCode().equals("1")) {
                                    Log.e("确定商品订单", "请求成功");
                                    goods.setG_state(1);
                                    goods.save();
                                    holder.tvMsg.setText("交易成功");
                                    holder.tvMsg.setTextColor(Color.GREEN);
                                    holder.tvConfirm.setVisibility(View.INVISIBLE);
                                    holder.tvRefuse.setVisibility(View.INVISIBLE);
                                    holder.tvDelete.setVisibility(View.VISIBLE);
                                    ToastUtil.showMsg(mContext, "确认订单成功", Toast.LENGTH_SHORT);
                                    if (onClick != null) {
                                        onClick.onConfirmClick(goods.getG_price());
                                    }
                                } else {
                                    Log.e("确定商品订单", "请求失败");
                                    ToastUtil.showMsg(mContext, "确认订单失败，请重试", Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    }
                });
            }
        });
        holder.tvRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick != null) {
                    onClick.onRefuseClick(goods);
                }
            }
        });
    }

    public void removeGoods(SellGoods sellGoods) {
        sellGoodsList.remove(sellGoods);
    }

    @Override
    public int getItemCount() {
        return sellGoodsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPic;
        TextView tvPrice, tvGoodsName, tvContact, tvDelete, tvMsg, tvConfirm, tvRefuse;
        LinearLayout llMain;

        public ViewHolder(@NonNull View view) {
            super(view);
            ivPic = view.findViewById(R.id.iv_mysell_item_pic);
            tvPrice = view.findViewById(R.id.tv_mysell_item_price);
            tvGoodsName = view.findViewById(R.id.tv_mysell_item_name);
            tvContact = view.findViewById(R.id.tv_mysell_item_contact);
            tvDelete = view.findViewById(R.id.tv_mysell_item_delete);
            tvMsg = view.findViewById(R.id.tv_mysell_item_msg);
            tvConfirm = view.findViewById(R.id.tv_mysell_item_confirm);
            tvRefuse = view.findViewById(R.id.tv_mysell_item_refuse);
            llMain = view.findViewById(R.id.ll_mysell_item_main);
        }
    }

    // 启动商品详情页面
    private void startGoodsInfoActivity(SellGoods sellGoods) {
        HomeDataBean.NewestInfoBean goods = new HomeDataBean.NewestInfoBean(sellGoods.getG_id(),
                sellGoods.getG_name(), sellGoods.getG_desc(), sellGoods.getG_price(),
                sellGoods.getG_originalPrice(), sellGoods.getG_pic1(), sellGoods.getG_pic2(),
                sellGoods.getG_pic3(), sellGoods.getG_state(), sellGoods.getG_like(),
                sellGoods.getG_updateTime(), sellGoods.getG_t_id(), user.getU_id(),
                user.getU_nick(), user.getU_photo(), user.getU_sex());
        Intent intent = new Intent(mContext, GoodsInfoActivity.class);
        intent.putExtra(GOODS_BEAN, goods);
        mContext.startActivity(intent);
    }

    public void setSellGoodsList(List<SellGoods> sellGoodsList) {
        this.sellGoodsList = sellGoodsList;
    }

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }

    public interface OnClick {
        void onDeleteClick(String g_id);
        void onConfirmClick(double g_price);
        void onRefuseClick(SellGoods sellGoods);
    }

}
