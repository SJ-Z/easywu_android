package com.cose.easywu.user.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.db.BuyGoods;
import com.cose.easywu.db.User;
import com.cose.easywu.home.activity.GoodsInfoActivity;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.message.activity.ChatActivity;
import com.cose.easywu.utils.Constant;
import com.hyphenate.easeui.EaseConstant;

import org.litepal.LitePal;

import java.util.List;

public class MyBuyAdapter extends RecyclerView.Adapter<MyBuyAdapter.ViewHolder> {

    private Context mContext;
    private List<BuyGoods> buyGoodsList;

    public static final String GOODS_BEAN = "goodsBean";

    public MyBuyAdapter(Context context, List<BuyGoods> buyGoodsList, String u_id) {
        mContext = context;
        this.buyGoodsList = buyGoodsList;
    }

    @NonNull
    @Override
    public MyBuyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mybuygoods, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyBuyAdapter.ViewHolder holder, final int position) {
        final BuyGoods goods = buyGoodsList.get(position);
        holder.tvPrice.setText(String.valueOf(goods.getG_price()));
        holder.tvGoodsName.setText(goods.getG_name());
        Glide.with(mContext).load(Constant.BASE_PIC_URL + goods.getG_pic1()).apply(new RequestOptions()
                .placeholder(R.drawable.ic_loading_pic).error(R.drawable.ic_error_goods)).into(holder.ivPic);
        if (goods.getG_state() == 1) {
            holder.tvMsg.setText("交易成功");
            holder.tvMsg.setTextColor(Color.GREEN);
        } else {
            holder.tvMsg.setText("等待卖家确认");
            holder.tvMsg.setTextColor(Color.RED);
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
                intent.putExtra(EaseConstant.EXTRA_USER_ID, goods.getG_u_id());
                // 传递商品信息
                intent.putExtra("isGoods", true);
                intent.putExtra("goods_id", goods.getG_id());
                intent.putExtra("goods_name", goods.getG_name());
                intent.putExtra("goods_pic", Constant.BASE_PIC_URL + goods.getG_pic1());
                intent.putExtra("goods_price", goods.getG_price());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return buyGoodsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPic;
        TextView tvPrice, tvGoodsName, tvContact, tvMsg;
        LinearLayout llMain;

        public ViewHolder(@NonNull View view) {
            super(view);
            ivPic = view.findViewById(R.id.iv_mysell_item_pic);
            tvPrice = view.findViewById(R.id.tv_mysell_item_price);
            tvGoodsName = view.findViewById(R.id.tv_mysell_item_name);
            tvContact = view.findViewById(R.id.tv_mysell_item_contact);
            tvMsg = view.findViewById(R.id.tv_mysell_item_msg);
            llMain = view.findViewById(R.id.ll_mysell_item_main);
        }
    }

    // 启动商品详情页面
    private void startGoodsInfoActivity(BuyGoods buyGoods) {
        HomeDataBean.NewestInfoBean goods = new HomeDataBean.NewestInfoBean(buyGoods.getG_id(),
                buyGoods.getG_name(), buyGoods.getG_desc(), buyGoods.getG_price(),
                buyGoods.getG_originalPrice(), buyGoods.getG_pic1(), buyGoods.getG_pic2(),
                buyGoods.getG_pic3(), buyGoods.getG_state(), buyGoods.getG_like(),
                buyGoods.getG_updateTime(), buyGoods.getG_t_id(), buyGoods.getG_u_id(),
                buyGoods.getG_u_nick(), buyGoods.getG_u_photo(), buyGoods.getG_u_sex());
        Intent intent = new Intent(mContext, GoodsInfoActivity.class);
        intent.putExtra(GOODS_BEAN, goods);
        mContext.startActivity(intent);
    }

    public void setBuyGoodsList(List<BuyGoods> buyGoodsList) {
        this.buyGoodsList = buyGoodsList;
    }

}
