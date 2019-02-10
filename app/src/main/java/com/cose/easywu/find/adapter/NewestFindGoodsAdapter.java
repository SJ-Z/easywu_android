package com.cose.easywu.find.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.find.bean.FindDataBean;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.DateUtil;

import java.util.Date;
import java.util.List;

public class NewestFindGoodsAdapter extends RecyclerView.Adapter<NewestFindGoodsAdapter.ViewHolder> {

    private Context mContext;
    private List<FindDataBean.FindNewestInfo> mGoodsList;

    public NewestFindGoodsAdapter(Context context, List<FindDataBean.FindNewestInfo> goodsList) {
        mContext = context;
        mGoodsList = goodsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_goods_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FindDataBean.FindNewestInfo goods = mGoodsList.get(position);
        Glide.with(mContext).load(Constant.BASE_FIND_PIC_URL + goods.getFg_pic1())
                .apply(new RequestOptions().placeholder(R.drawable.ic_loading_pic).error(R.drawable.ic_error_goods))
                .into(holder.ivGoodsPic);
        Glide.with(mContext).load(Constant.BASE_PHOTO_URL +goods.getFg_u_photo())
                .apply(new RequestOptions().placeholder(R.drawable.nav_icon))
                .into(holder.ivUserPhoto);
        holder.tvGoodsName.setText(goods.getFg_name());
        holder.tvUserNick.setText(goods.getFg_u_nick());
        holder.tvUpdateTime.setText(DateUtil.getDatePoor(goods.getFg_updateTime(), new Date()));
    }

    @Override
    public int getItemCount() {
        return mGoodsList == null ? 0 : mGoodsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGoodsPic, ivUserPhoto;
        TextView tvGoodsName, tvUserNick, tvUpdateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGoodsPic = itemView.findViewById(R.id.iv_find_goods_pic);
            tvGoodsName = itemView.findViewById(R.id.tv_find_goods_goods_name);
            ivUserPhoto = itemView.findViewById(R.id.iv_find_goods_user_photo);
            tvUserNick = itemView.findViewById(R.id.tv_find_goods_user_nick);
            tvUpdateTime = itemView.findViewById(R.id.tv_find_goods_update_time);
        }
    }

}
