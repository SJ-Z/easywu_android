package com.cose.easywu.user.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.cose.easywu.db.LikeGoods;
import com.cose.easywu.db.SellGoods;
import com.cose.easywu.db.User;
import com.cose.easywu.home.activity.GoodsInfoActivity;
import com.cose.easywu.home.bean.HomeDataBean;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.DateUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MySellAdapter extends RecyclerView.Adapter<MySellAdapter.ViewHolder> {

    private Context mContext;
    private List<SellGoods> sellGoodsList;
    private OnDeleteClick onDeleteClick;
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
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.tvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.tvGoodsDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGoodsInfoActivity(goods);
            }
        });
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteClick != null) {
                    onDeleteClick.click(goods.getG_id());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return sellGoodsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPic;
        TextView tvPrice, tvGoodsName, tvContact, tvGoodsDesc, tvDelete;
        LinearLayout llMain;

        public ViewHolder(@NonNull View view) {
            super(view);
            ivPic = view.findViewById(R.id.iv_mysell_item_pic);
            tvPrice = view.findViewById(R.id.tv_mysell_item_price);
            tvGoodsName = view.findViewById(R.id.tv_mysell_item_name);
            tvContact = view.findViewById(R.id.tv_mysell_item_contact);
            tvGoodsDesc = view.findViewById(R.id.tv_mysell_item_goods_desc);
            tvDelete = view.findViewById(R.id.tv_mysell_item_delete);
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

    public void setOnDeleteClick(OnDeleteClick onDeleteClick) {
        this.onDeleteClick = onDeleteClick;
    }

    public interface OnDeleteClick {
        void click(String g_id);
    }

}
